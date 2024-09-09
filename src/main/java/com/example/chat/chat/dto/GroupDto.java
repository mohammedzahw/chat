package com.example.chat.chat.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.chat.chat.model.GroupPrivacy;
import com.example.chat.registration.dto.LocalUserDto;

import lombok.Data;

@Data
@SuppressWarnings("rawtypes")
public class GroupDto {

    private Integer id;
    private String name;
    private String description;
    private String imageUrl;
    private GroupPrivacy privacy;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdated;
    private LocalUserDto owner;
    private List<MessageDto> messages;

}
