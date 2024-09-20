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
import com.example.chat.chat.model.Channel;
import com.example.chat.chat.model.MessageChannel;
import com.example.chat.chat.model.MessageType;
import com.example.chat.chat.service.MessageChannelService;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Component

public class MessageChannelMapper {
    private final MessageChannelReactionMapper messageChannelReactionMapper;
    private final MessageChannelService messageChannelService;

    public MessageChannelMapper(MessageChannelReactionMapper messageChannelReactionMapper,
            @Lazy MessageChannelService messageChannelService) {
        this.messageChannelReactionMapper = messageChannelReactionMapper;
        this.messageChannelService = messageChannelService;
    }

    public MessageDto toDto(MessageChannel messageChannel) {
        if (messageChannel == null) {
            return null;
        }

        MessageDto messageDto = new MessageDto();

        messageDto.setContent(messageChannel.getContent());
        if (messageChannel.getId() != null) {
            messageDto.setId(messageChannel.getId());
        }
        messageDto.setParentMessage(toDto(messageChannel.getParentMessage()));

        List<MessageReactionDto> list = messageChannelReactionMapper.toDtoList(messageChannel.getReactions());
        if (list != null) {
            messageDto.setReactions(new ArrayList(list));
        }
        messageDto.setSendDateTime(messageChannel.getSendDateTime());
        messageDto.setType(messageChannel.getType());

        return messageDto;
    }

    /********************************************************************************************* */
    public MessageChannel toEntity(SendTextMessageDto sendMessageDto, Channel channel) {
        if (sendMessageDto == null) {
            return null;
        }
        MessageChannel parent = null;
        if (sendMessageDto.getParentMessageId() != null)
            parent = messageChannelService.getMessageById(sendMessageDto.getParentMessageId());

        MessageChannel messageChannel = new MessageChannel();
        messageChannel.setChannel(channel);
        messageChannel.setParentMessage(parent);
        messageChannel.setContent(sendMessageDto.getText());
        messageChannel.setDuration(0.0);
        messageChannel.setSize(0.0);
        messageChannel.setType(MessageType.TEXT);
        messageChannel.setSendDateTime(LocalDateTime.now());

        return messageChannel;

    }
    public MessageChannel toMediaEntity(SendMessageDto sendMessageDto, Channel channel, String url, String publicId, Double duration, Double size) {
        if (sendMessageDto == null) {
            return null;
        }
        MessageChannel parent = null;
        if (sendMessageDto.getParentMessageId() != null)
            parent = messageChannelService.getMessageById(sendMessageDto.getParentMessageId());

        MessageChannel messageChannel = new MessageChannel();
        messageChannel.setChannel(channel);
        messageChannel.setParentMessage(parent);
        messageChannel.setContent(url);
        messageChannel.setDuration(duration);
        messageChannel.setSize(size);
        messageChannel.setUrlId(publicId);
        messageChannel.setType( sendMessageDto.getType());
        messageChannel.setSendDateTime(LocalDateTime.now());

        return messageChannel;

    }

    /********************************************************************************************* */

    public List<MessageDto> toDtoList(List<MessageChannel> messagesByChannelId) {
        if (messagesByChannelId == null) {
            return null;
        }

        List<MessageDto> list = new ArrayList<MessageDto>(messagesByChannelId.size());
        for (MessageChannel messageChannel : messagesByChannelId) {
            list.add(toDto(messageChannel));
        }

        return list;
    }
    /********************************************************************************************* */
}
