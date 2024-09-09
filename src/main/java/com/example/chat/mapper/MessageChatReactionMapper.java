package com.example.chat.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.chat.chat.dto.MessageReactionDto;
import com.example.chat.chat.model.MessageChatReaction;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessageChatReactionMapper {
    private final LocalUserMapper localUserMapper;

    public MessageReactionDto toDto(MessageChatReaction messageChatReaction) {
        if (messageChatReaction == null) {
            return null;
        }

        MessageReactionDto messageReactionDto = new MessageReactionDto();

        messageReactionDto.setId(messageChatReaction.getId());
        messageReactionDto.setReaction(messageChatReaction.getReaction());
        messageReactionDto.setReactor(localUserMapper.toDto(messageChatReaction.getReactor()));

        return messageReactionDto;
    }

    /********************************************************************************************* */

    public List<MessageReactionDto> toDtoList(List<MessageChatReaction> reactions) {
        if (reactions == null) {
            return null;
        }

        List<MessageReactionDto> list = new ArrayList<MessageReactionDto>(reactions.size());
        for (MessageChatReaction messageChatReaction : reactions) {
            list.add(toDto(messageChatReaction));
        }

        return list;
    }
    /********************************************************************************************* */

}
