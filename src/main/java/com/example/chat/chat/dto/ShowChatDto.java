package com.example.chat.chat.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@SuppressWarnings("rawtypes")
public class ShowChatDto {

    private Integer id;

    private String name;

    private String imageUrl;

    private Long numberOfUreadMessages;

    private MessageDto lastMessage;

    public ShowChatDto(Integer id, String name, Long numberOfUreadMessages, MessageDto lastMessage) {
        this.id = id;

        this.name = name;
        this.numberOfUreadMessages = numberOfUreadMessages;
        this.lastMessage = lastMessage;
    }

}
