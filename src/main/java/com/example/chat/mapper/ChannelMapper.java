package com.example.chat.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.chat.chat.dto.ChannelDto;
import com.example.chat.chat.model.Channel;

@Mapper
public interface ChannelMapper {
    ChannelMapper INSTANCE = Mappers.getMapper(ChannelMapper.class);

    ChannelDto toDto(Channel channel);

}
