package com.example.chat.chat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.chat.chat.model.MessageGroupReaction;

public interface MessageGroupReactionRepository extends JpaRepository<MessageGroupReaction, Integer> {

    @Query("SELECT mgs FROM MessageGroupReaction mgs WHERE mgs.messageGroup.id = :messageId AND mgs.reactor.id = :id")
    Optional<MessageGroupReaction> getReactionByMessageIdAndUserId(Integer messageId, Integer id);

}
