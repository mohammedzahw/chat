package com.example.chat.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageDto {

    private String content;
    private String type;
    private Integer Id;
    private Integer parentMessageId;
}
