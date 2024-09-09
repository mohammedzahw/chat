package com.example.chat.chat.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.chat.chat.dto.ChatDto;
import com.example.chat.chat.dto.MessageDto;
import com.example.chat.chat.dto.ShowChatDto;
import com.example.chat.chat.model.Chat;
import com.example.chat.chat.model.MessageChat;
import com.example.chat.chat.model.Queue;
import com.example.chat.chat.repository.ChatRepository;
import com.example.chat.exception.CustomException;
import com.example.chat.mapper.ChatMapper;
import com.example.chat.mapper.LocalUserMapper;
import com.example.chat.mapper.MessageChatMapper;
import com.example.chat.registration.Service.LocalUserService;
import com.example.chat.registration.model.LocalUser;

@Service

public class ChatService {
    private final ChatMapper chatMapper;
    private final MessageChatMapper messageChatMapper;
    private final LocalUserMapper localUserMapper;
    private final QueueService queueService;

    private final ChatRepository chatRepository;

    private final LocalUserService localUserService;
    private final MessageChatService messageChatService;

    public ChatService(ChatMapper chatMapper, MessageChatMapper messageChatMapper, LocalUserMapper localUserMapper,
            QueueService queueService, ChatRepository chatRepository, LocalUserService localUserService,
            @Lazy MessageChatService messageChatService) {
        this.chatMapper = chatMapper;
        this.messageChatMapper = messageChatMapper;
        this.localUserMapper = localUserMapper;
        this.queueService = queueService;
        this.chatRepository = chatRepository;
        this.localUserService = localUserService;
        this.messageChatService = messageChatService;
    }

    /**************************************************************************************** */
    @Transactional
    public Chat createChat(Integer user2Id) throws IOException, TimeoutException {

        LocalUser user1 = localUserService.getLocalUserByToken();
        LocalUser user2 = localUserService.getLocalUserById(user2Id);

        Chat chat = chatRepository.findByUser1AndUser2(user1.getId(), user2Id).orElse(null);

        if (chat != null) {
            return chat;
        }

        chat = new Chat();
        chatRepository.save(chat);

        chat.setUser1(user1);
        chat.setUser2(user2);
        chat.setLastUpdated(LocalDateTime.now());
        chat.setCreatedDate(LocalDateTime.now());
        Queue queue = queueService.getQueue(user1.getEmail() + user2.getEmail());
        if (queue == null) {

            queue = queueService.createQueue(user1.getEmail() + user2.getEmail(), user1.getId() + "" + user2.getId(),
                    user1.getId() + "" + user2.getId());

        }

        chat.setQueue(queue);
        chatRepository.save(chat);

        localUserService.addQueue(user2Id, queue.getId());
        localUserService.addChat(user2Id, chat.getId());
        localUserService.addQueue(user1.getId(), queue.getId());
        localUserService.addChat(user1.getId(), chat.getId());

        return chat;
    }

    /**************************************************************************************** */

    @SuppressWarnings("rawtypes")
    public List<ShowChatDto> getUserChats() {

        LocalUser user = localUserService.getLocalUserByToken();

        List<Chat> chats = chatRepository.findChatByUser(user.getId());
        if (chats == null) {
            return new ArrayList<>();

        }
        List<ShowChatDto> chatDtos = new ArrayList<>();
        for (Chat c : chats) {
            LocalUser sender;

            if (c.getUser1().getId() == user.getId()) {
                sender = c.getUser2();
            } else {
                sender = c.getUser1();
            }
            MessageChat lastMessage = messageChatService.getLastMessageByChatId(c.getId());
            MessageDto messageDto = null;
            if (lastMessage != null) {
                messageDto = messageChatMapper.toDto(lastMessage);
            }
            Long unreadCount = messageChatService.getNumberOfUreadMessageByChatId(c.getId());

            ShowChatDto showChatDto = new ShowChatDto(c.getId(), sender.getName(), sender.getImageUrl(),
                    unreadCount, messageDto);
            chatDtos.add(showChatDto);

        }

        return chatDtos;
    }

    /**************************************************************************************** */
    public Chat getChat(Integer chatId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new CustomException("Chat not found", HttpStatus.NOT_FOUND));
        return chat;
    }

    /**************************************************************************************** */
    public ChatDto getChatWithMessages(Integer chatId) {

        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new CustomException("Chat not found", HttpStatus.NOT_FOUND));

        LocalUser user = localUserService.getLocalUserByToken();
        LocalUser sender = chat.getUser1().getId() == user.getId() ? chat.getUser2() : chat.getUser1();
        List<MessageChat> messages = messageChatService.getMessagesByChatId(chatId, 0);

        chat.setMessages(messages);
        ChatDto chatDto = chatMapper.toDto(chat);
        chatDto.setName(sender.getName());
        chatDto.setSender(localUserMapper.toDto(sender));
        return chatDto;

    }

    public void saveChat(Chat chat) {
        chatRepository.save(chat);
    }

    /**************************************************************************************** */

    public List<String> getQueueList() {
        // System.out.println(tokenUtil.getUserId());
        return localUserService.getQueuesByUserId();
    }

    /*** ***************************************************************************************/
@Transactional
    public void deleteChat(Integer chatId) throws IOException, TimeoutException {
        LocalUser user = localUserService.getLocalUserByToken();

        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new CustomException("Chat not found", HttpStatus.NOT_FOUND));
        if (chat.getUser1().getId() != user.getId() && chat.getUser2().getId() != user.getId()) {
            throw new CustomException("You can't delete this chat", HttpStatus.BAD_REQUEST);
        }
        Queue queue = chat.getQueue();
        chatRepository.deleteById(chatId);
        queueService.deleteQueueFromRabbitMq(queue.getName());
        queueService.deleteQueue(queue.getId());

    }
}
