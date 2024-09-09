package com.example.chat.chat.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.chat.chat.model.MessageStatus;
import com.example.chat.registration.dto.LocalUserDto;

import lombok.Data;

@Data
@SuppressWarnings("rawtypes")
public class MessageDto<T> {
    private Integer id;
    private String content;
    private String type;
    private MessageStatus status;
    private LocalDateTime sendDateTime;
    private LocalUserDto sender;
    private LocalDateTime receiveDateTime;
    private List<T> reactions;

    private MessageDto parentMessage;
    // private GroupDto group;
}
