package com.example.chat.chat.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.chat.chat.dto.SendMessageDto;
import com.example.chat.chat.model.Chat;
import com.example.chat.chat.model.MessageChat;
import com.example.chat.chat.model.MessageChatReaction;
import com.example.chat.chat.model.MessageReaction;
import com.example.chat.chat.model.Queue;
import com.example.chat.chat.repository.MessageChatReactionRepository;
import com.example.chat.chat.repository.MessageChatRepository;
import com.example.chat.exception.CustomException;
import com.example.chat.mapper.MessageChatMapper;
import com.example.chat.registration.Service.LocalUserService;
import com.example.chat.registration.model.LocalUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageChatService {
        @Lazy
        private final MessageChatMapper messageChatMapper;
        private final QueueService queueService;

        private final LocalUserService localUserService;

        @Lazy
        private final ChatService chatService;
        private final MessageChatReactionRepository messageChatReactionRepository;

        private final MessageChatRepository messageChatRepository;

        // public MessageChatService(@Lazy MessageChatMapper messageChatMapper,
        // QueueService queueService,
        // LocalUserService localUserService, @Lazy ChatService chatService,
        // MessageChatReactionRepository messageChatReactionRepository,
        // MessageChatRepository messageChatRepository) {
        // this.messageChatMapper = messageChatMapper;
        // this.queueService = queueService;
        // this.localUserService = localUserService;
        // this.chatService = chatService;
        // this.messageChatReactionRepository = messageChatReactionRepository;
        // this.messageChatRepository = messageChatRepository;
        // }

        /******************************************************************************* */
        public void sendMessage(SendMessageDto sendMessageDto) throws IOException, TimeoutException {
                Chat chat = chatService.getChat(sendMessageDto.getId());
                LocalUser sender = localUserService.getLocalUserByToken();
                if (chat.getUser1().getId() != sender.getId() && chat.getUser2().getId() != sender.getId()) {
                        throw new CustomException("You can't send this message", HttpStatus.BAD_REQUEST);
                }

                Queue queue = chat.getQueue();

                MessageChat messageChat = messageChatMapper.toEntity(sendMessageDto, chat, sender);
                messageChatRepository.save(messageChat);
                chat.setLastUpdated(LocalDateTime.now());
                chatService.saveChat(chat);

                queueService.sendMessage(queue,
                                messageChatMapper.toDto(messageChat), "Chat");

        }

        /******************************************************************************* */

        public MessageChat getMessageById(int int1) {
                return messageChatRepository.findById(int1).orElse(null);
        }

        /******************************************************************************* */
        public MessageChat saveMessageChat(MessageChat messageChat) {
                return messageChatRepository.save(messageChat);
        }

        /******************************************************************************* */

        public MessageChat getLastMessageByChatId(Integer id) {
                List<MessageChat> messageChats = messageChatRepository.getMessagesByChatId(id, PageRequest.of(0, 1));

                if (messageChats.isEmpty()) {
                        return null;
                }
                return messageChats.get(0);
        }

        /******************************************************************************* */
        public Long getNumberOfUreadMessageByChatId(Integer id) {
                return messageChatRepository.getNumberOfUnreadMessage(id);
        }

        /******************************************************************************* */

        public List<MessageChat> getMessagesByChatId(Integer chatId, Integer pageNumber) {
                return messageChatRepository.getMessagesByChatId(chatId, PageRequest.of(pageNumber, 20));
        }

        /******************************************************************************* */

        public void reactMessage(Integer messageId, MessageReaction reaction) {
                LocalUser sender = localUserService.getLocalUserByToken();
                MessageChatReaction messageChatReaction = getReactionByMessageIdAndUserId(messageId, sender.getId());
                if (messageChatReaction == null) {
                        messageChatReaction = new MessageChatReaction();
                        messageChatReaction.setMessageChat(getMessageById(messageId));
                        messageChatReaction.setReactor(sender);
                        messageChatReaction.setReaction(reaction);
                }

                messageChatReaction.setReaction(reaction);
                messageChatReactionRepository.save(messageChatReaction);

        }

        /******************************************************************************* */

        /******************************************************************************* */

        private MessageChatReaction getReactionByMessageIdAndUserId(Integer messageId, Integer id) {

                return messageChatReactionRepository.getReactionByMessageIdAndUserId(messageId, id).orElse(null);

        }

        public void unReactMessage(Integer messageId) {

                LocalUser user = localUserService.getLocalUserByToken();
                MessageChatReaction messageChatReaction = getReactionByMessageIdAndUserId(messageId, user.getId());
                if (messageChatReaction != null) {
                        messageChatReactionRepository.delete(messageChatReaction);
                }
        }

        /******************************************************************************* */
        public void deleteMessage(Integer messageId) {

                messageChatRepository.deleteById(messageId);
        }
}
