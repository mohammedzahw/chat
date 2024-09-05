package com.example.chat.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MessageChannelMapper {
    MessageChannelMapper INSTANCE = Mappers.getMapper(MessageChannelMapper.class);

}
