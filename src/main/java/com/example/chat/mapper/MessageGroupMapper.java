package com.example.chat.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.chat.chat.dto.MessageDto;
import com.example.chat.chat.model.MessageGroup;

@Mapper
public interface MessageGroupMapper {

    MessageGroupMapper INSTANCE = Mappers.getMapper(MessageGroupMapper.class);

    MessageDto toDto(MessageGroup messageGroup);

}
