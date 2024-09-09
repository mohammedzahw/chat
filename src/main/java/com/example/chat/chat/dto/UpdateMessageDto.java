package com.example.chat.chat.dto;

import lombok.Data;

@Data
public class UpdateMessageDto {

    private String content;
    private String type;
    private Integer messageId;

}
