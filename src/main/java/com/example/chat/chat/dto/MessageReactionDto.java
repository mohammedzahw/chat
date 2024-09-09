package com.example.chat.chat.dto;

import com.example.chat.chat.model.MessageReaction;
import com.example.chat.registration.dto.LocalUserDto;

import lombok.Data;

@Data
public class MessageReactionDto {
    private MessageReaction reaction;
    private Integer id;
    private LocalUserDto reactor;

}
