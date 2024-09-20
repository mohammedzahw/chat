package com.example.chat.chat.dto;

import com.example.chat.chat.model.MessageType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendTextMessageDto {
    private String text;
    private Integer Id;
    private Integer parentMessageId;
}
