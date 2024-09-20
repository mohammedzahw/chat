package com.example.chat.chat.dto;

import java.time.LocalDateTime;

import com.example.chat.chat.model.MessageType;

import lombok.Data;

@Data
public class ShowChannelDto {
    private Integer id;
    private String imageUrl;

    private Long numberOfUreadMessages;
    private String name;

    private LocalDateTime lastMessageSendDate;

    private MessageType lastMessageType;
    private String lastMessageContent;

}
