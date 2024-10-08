package com.example.chat.chat.dto;

import org.springframework.web.multipart.MultipartFile;

import com.example.chat.chat.model.MessageType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageDto {
    private MultipartFile file;
    private MessageType type;
    private Integer Id;
    private Integer parentMessageId;
}
