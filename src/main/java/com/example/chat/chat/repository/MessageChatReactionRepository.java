package com.example.chat.chat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.chat.chat.model.MessageChatReaction;

public interface MessageChatReactionRepository extends JpaRepository<MessageChatReaction, Integer> {

    @Query("SELECT m FROM MessageChatReaction m WHERE m.messageChat.id = :messageId AND m.reactor.id = :id")
    Optional<MessageChatReaction> getReactionByMessageIdAndUserId(Integer messageId, Integer id);

}
