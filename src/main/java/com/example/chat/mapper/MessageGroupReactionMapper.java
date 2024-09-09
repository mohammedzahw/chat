package com.example.chat.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.chat.chat.dto.MessageReactionDto;
import com.example.chat.chat.model.MessageGroupReaction;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessageGroupReactionMapper {

    private final LocalUserMapper localUserMapper;

    public MessageReactionDto toDto(MessageGroupReaction messageGroupReaction) {
        if (messageGroupReaction == null) {
            return null;
        }

        MessageReactionDto messageReactionDto = new MessageReactionDto();

        messageReactionDto.setId(messageGroupReaction.getId());
        messageReactionDto.setReaction(messageGroupReaction.getReaction());
        messageReactionDto.setReactor(localUserMapper.toDto(messageGroupReaction.getReactor()));

        return messageReactionDto;
    }

    /************************************************************************************************ */

    public List<MessageReactionDto> toDtoList(List<MessageGroupReaction> reactions) {
        if (reactions == null) {
            return null;
        }

        List<MessageReactionDto> list = new ArrayList<MessageReactionDto>(reactions.size());
        for (MessageGroupReaction messageGroupReaction : reactions) {
            list.add(toDto(messageGroupReaction));
        }

        return list;
    }

    /************************************************************************************************ */

}
