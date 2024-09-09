package com.example.chat.chat.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.chat.chat.model.MessageChat;

public interface MessageChatRepository extends JpaRepository<MessageChat, Integer> {

    // @Query("SELECT m.receiver FROM MessageChat m WHERE m.id = :MessageChatId")
    // Optional<LocalUser> getReceiverByMessageChatId(Integer MessageChatId);

    // get last message
    @Query("SELECT m FROM MessageChat m WHERE m.chat.id = :chatId ORDER BY m.sendDateTime DESC")
    List<MessageChat> getMessagesByChatId(Integer chatId, Pageable pageable);

    @Query("SELECT count(m) FROM MessageChat m WHERE m.chat.id = :id AND m.status = 'SENT'")
    Long getNumberOfUnreadMessage(Integer id);


}
