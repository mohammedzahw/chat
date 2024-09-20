package com.example.chat.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.example.chat.chat.dto.MessageDto;
import com.example.chat.chat.dto.MessageReactionDto;
import com.example.chat.chat.dto.SendMessageDto;
import com.example.chat.chat.dto.SendTextMessageDto;
import com.example.chat.chat.model.Chat;
import com.example.chat.chat.model.MessageChat;
import com.example.chat.chat.model.MessageStatus;
import com.example.chat.chat.model.MessageType;
import com.example.chat.chat.service.MessageChatService;
import com.example.chat.registration.model.LocalUser;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Component

public class MessageChatMapper {

    private final LocalUserMapper localUserMapper;
    private final MessageChatReactionMapper messageChatReactionMapper;

    private final MessageChatService messageChatService;

    public MessageChatMapper(LocalUserMapper localUserMapper,
            MessageChatReactionMapper messageChatReactionMapper,
            @Lazy MessageChatService messageChatService) {
        this.localUserMapper = localUserMapper;
        this.messageChatReactionMapper = messageChatReactionMapper;
        this.messageChatService = messageChatService;
    }

    public MessageDto toDto(MessageChat messageChat) {
        if (messageChat == null) {
            return null;
        }

        MessageDto messageDto = new MessageDto();

        messageDto.setContent(messageChat.getContent());
        if (messageChat.getId() != null) {
            messageDto.setId(messageChat.getId());
        }
        messageDto.setParentMessage(toDto(messageChat.getParentMessage()));
        List<MessageReactionDto> list = messageChatReactionMapper.toDtoList(messageChat.getReactions());
        if (list != null) {
            messageDto.setReactions(new ArrayList(list));
        }
        messageDto.setReceiveDateTime(messageChat.getReceiveDateTime());
        messageDto.setSendDateTime(messageChat.getSendDateTime());
        messageDto.setSender(localUserMapper.toDto(messageChat.getSender()));
        messageDto.setStatus(messageChat.getStatus());
        messageDto.setType(messageChat.getType());

        return messageDto;
    }

    /********************************************************************************************* */
    public MessageChat toEntity(SendTextMessageDto sendMessageDto, Chat chat, LocalUser sender) {
        if (sendMessageDto == null) {
            return null;
        }
        MessageChat parent = null;
        if (sendMessageDto.getParentMessageId() != null)
            parent = messageChatService.getMessageById(sendMessageDto.getParentMessageId());

        MessageChat messageChat = new MessageChat();
        messageChat.setChat(chat);
        messageChat.setParentMessage(parent);
        messageChat.setContent(sendMessageDto.getText());
        messageChat.setDuration(0.0);
        messageChat.setSize(0.0);
        messageChat.setType(MessageType.TEXT);
        messageChat.setSendDateTime(LocalDateTime.now());
        messageChat.setSender(sender);
        messageChat.setStatus(MessageStatus.SENT);

        return messageChat;
    }

    /********************************************************************************************* */

    public List<MessageDto> toDtoList(List<MessageChat> messageChats) {
        if (messageChats == null) {
            return null;
        }

        List<MessageDto> list = new ArrayList<MessageDto>(messageChats.size());
        for (MessageChat messageChat : messageChats) {
            list.add(toDto(messageChat));
        }

        return list;
    }

    public MessageChat toMediaEntity(SendMessageDto sendMessageDto, Chat chat, String url, String publicId,
    Double duration, Double size, LocalUser sender) {
                if (sendMessageDto == null) {
                    return null;
                }
                MessageChat parent = null;
                if (sendMessageDto.getParentMessageId() != null)
                    parent = messageChatService.getMessageById(sendMessageDto.getParentMessageId());
        
                MessageChat messageChat = new MessageChat();
                messageChat.setChat(chat);
                messageChat.setParentMessage(parent);
                messageChat.setContent(url);
                messageChat.setType(sendMessageDto.getType());
                messageChat.setDuration(duration);
                messageChat.setSize(size);
                messageChat.setUrlId(publicId);
        
                messageChat.setSendDateTime(LocalDateTime.now());
                messageChat.setSender(sender);
                messageChat.setStatus(MessageStatus.SENT);
        
                return messageChat;
    }

    /********************************************************************************************* */

}
