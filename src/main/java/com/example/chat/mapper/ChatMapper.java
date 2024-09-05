package com.example.chat.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.chat.chat.dto.ChatDto;
import com.example.chat.chat.model.Chat;

@Mapper
public interface ChatMapper {
    ChatMapper INSTANCE = Mappers.getMapper(ChatMapper.class);

    public ChatDto toDto(Chat chat);

    // public Chat toEntity(ChatDto chatDto);

    public List<ChatDto> toDtoList(List<Chat> chats);

    // public List<Chat> toEntityList(List<ChatDto> chatDtos);
}
