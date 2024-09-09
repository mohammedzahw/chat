package com.example.chat.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.chat.chat.dto.MessageDto;
import com.example.chat.chat.dto.MessageReactionDto;
import com.example.chat.chat.model.MessageGroup;

import lombok.RequiredArgsConstructor;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Component
@RequiredArgsConstructor
public class MessageGroupMapper {
    private final LocalUserMapper localUserMapper;
    private final MessageGroupReactionMapper messageGroupReactionMapper;

    public MessageDto toDto(MessageGroup messageGroup) {
        if (messageGroup == null) {
            return null;
        }

        MessageDto messageDto = new MessageDto();

        messageDto.setContent(messageGroup.getContent());
        if (messageGroup.getId() != null) {
            messageDto.setId(messageGroup.getId());
        }
        messageDto.setParentMessage(toDto(messageGroup.getParentMessage()));
        List<MessageReactionDto> list = messageGroupReactionMapper.toDtoList(messageGroup.getReactions());
        if (list != null) {
            messageDto.setReactions(new ArrayList(list));
        }
        messageDto.setSendDateTime(messageGroup.getSendDateTime());
        messageDto.setSender(localUserMapper.toDto(messageGroup.getSender()));
        messageDto.setType(messageGroup.getType());

        return messageDto;
    }

    /********************************************************************************************* */

    public List<MessageDto> toDtoList(List<MessageGroup> messagesByGroupId) {
        if (messagesByGroupId == null) {
            return null;
        }

        List<MessageDto> list = new ArrayList<MessageDto>(messagesByGroupId.size());
        for (MessageGroup messageGroup : messagesByGroupId) {
            list.add(toDto(messageGroup));
        }

        return list;
    }
    /********************************************************************************************* */

}
