package com.example.chat.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.chat.chat.dto.MessageReactionDto;
import com.example.chat.chat.model.MessageChannelReaction;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessageChannelReactionMapper {
    private final LocalUserMapper localUserMapper;

    public MessageReactionDto toDto(MessageChannelReaction messageChannelReaction) {
        if (messageChannelReaction == null) {
            return null;
        }

        MessageReactionDto messageReactionDto = new MessageReactionDto();

        messageReactionDto.setId(messageChannelReaction.getId());
        messageReactionDto.setReaction(messageChannelReaction.getReaction());
        messageReactionDto.setReactor(localUserMapper.toDto(messageChannelReaction.getReactor()));

        return messageReactionDto;
    }

    /********************************************************************************************* */

    public List<MessageReactionDto> toDtoList(List<MessageChannelReaction> messageChannelReactions) {
        if (messageChannelReactions == null) {
            return null;
        }

        List<MessageReactionDto> list = new ArrayList<MessageReactionDto>(messageChannelReactions.size());
        for (MessageChannelReaction messageChannelReaction : messageChannelReactions) {
            list.add(toDto(messageChannelReaction));
        }

        return list;
    }

    /********************************************************************************************* */

}
