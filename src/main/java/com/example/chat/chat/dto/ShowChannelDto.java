package com.example.chat.chat.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ShowChannelDto {
    private Integer id;
    private String imageUrl;

    private Long numberOfUreadMessages;
    private String name;

    private LocalDateTime lastMessageSendDate;

    private String lastMessageType;
    private String lastMessageContent;

}
