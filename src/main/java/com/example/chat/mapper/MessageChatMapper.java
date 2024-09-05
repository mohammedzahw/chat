package com.example.chat.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.chat.chat.dto.MessageDto;
import com.example.chat.chat.model.MessageChat;

@Mapper
public interface MessageChatMapper {
    MessageChatMapper INSTANCE = Mappers.getMapper(MessageChatMapper.class);

    public MessageDto toDto(MessageChat messageChat);

    public List<MessageDto> toDtoList(List<MessageChat> messageChats);

    public MessageChat toEntity(MessageDto messageChatDto);
}
