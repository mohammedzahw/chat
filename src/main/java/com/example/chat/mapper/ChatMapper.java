package com.example.chat.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.chat.chat.dto.ChatDto;
import com.example.chat.chat.model.Chat;

@Component
public class ChatMapper {
    private final MessageChatMapper messageChatMapper;

    public ChatMapper(MessageChatMapper messageChatMapper) {
        this.messageChatMapper = messageChatMapper;
    }

    /********************************************************************************* */

    public ChatDto toDto(Chat chat) {
        if (chat == null) {
            return null;
        }

        ChatDto chatDto = new ChatDto();

        chatDto.setCreatedDate(chat.getCreatedDate());
        chatDto.setId(chat.getId());
        chatDto.setMessages(messageChatMapper.toDtoList(chat.getMessages()));

        return chatDto;
    }

    /********************************************************************************* */

    public List<ChatDto> toDtoList(List<Chat> chats) {
        if (chats == null) {
            return null;
        }

        List<ChatDto> list = new ArrayList<ChatDto>(chats.size());
        for (Chat chat : chats) {
            list.add(toDto(chat));
        }

        return list;
    }
    /********************************************************************************* */
}
