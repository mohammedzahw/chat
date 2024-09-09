package com.example.chat.chat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.chat.chat.model.MessageChannelReaction;

public interface MessageChannelReactionRepository extends JpaRepository<MessageChannelReaction, Long> {

    @Query("SELECT m FROM MessageChannelReaction m WHERE m.messageChannel.id = :messageId AND m.reactor.id = :userId")
    Optional<MessageChannelReaction> getReactionByMessageIdAndUserId(Integer messageId, Integer userId);

}
