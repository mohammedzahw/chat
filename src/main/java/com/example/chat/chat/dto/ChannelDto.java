package com.example.chat.chat.dto;

import java.util.List;

import com.example.chat.registration.dto.LocalUserDto;

import lombok.Data;

@Data
@SuppressWarnings("rawtypes")
public class ChannelDto {
    private Integer id;
    private String name;
    private String description;
    private String imageUrl;

    private LocalUserDto owner;


    private List<MessageDto> messages;
  
}