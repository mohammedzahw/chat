package com.example.chat.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.chat.chat.dto.GroupDto;
import com.example.chat.chat.model.Group;

@Mapper
public interface GroupMapper {


     GroupMapper INSTANCE = Mappers.getMapper(GroupMapper.class);

    public GroupDto toDto(Group group);

}
