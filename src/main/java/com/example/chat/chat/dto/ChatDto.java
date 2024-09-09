package com.example.chat.chat.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.chat.registration.dto.LocalUserDto;

import lombok.Data;

@Data
@SuppressWarnings("rawtypes")
public class ChatDto {
    private Integer id;

    private String name;

    private String imageUrl;
    private LocalDateTime createdDate;

    private LocalUserDto sender;
    private List<MessageDto> messages;

    // public ChatDto(Integer id, String name, String imageUrl, Long
    // numberOfUreadMessages,
    // List<MessageDto> messages) {
    // this.id = id;
    // this.imageUrl = imageUrl;
    // this.name = name;
    // this.Messages = messages;
    // }
}
