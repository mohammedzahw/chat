package com.example.chat.mapper;

import org.springframework.stereotype.Component;

import com.example.chat.chat.dto.GroupDto;
import com.example.chat.chat.model.Group;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GroupMapper {
    private final MessageGroupMapper messageGroupMapper;
    private final LocalUserMapper localUserMapper;

    public GroupDto toDto(Group group) {
        if (group == null) {
            return null;
        }

        GroupDto groupDto = new GroupDto();

        groupDto.setCreatedDate(group.getCreatedDate());
        groupDto.setDescription(group.getDescription());
        groupDto.setId(group.getId());
        groupDto.setLastUpdated(group.getLastUpdated());
        groupDto.setMessages(messageGroupMapper.toDtoList(group.getMessages()));
        groupDto.setName(group.getName());
        groupDto.setOwner(localUserMapper.toDto(group.getOwner()));
        groupDto.setPrivacy(group.getPrivacy());
        if (group.getImageGroup() != null) {
            groupDto.setImageUrl(group.getImageGroup().getImageUrl());
        }

        return groupDto;
    }
    /********************************************************************************************* */

}