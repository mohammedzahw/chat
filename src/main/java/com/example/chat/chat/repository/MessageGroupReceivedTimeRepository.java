package com.example.chat.chat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.chat.chat.model.MessageGroupReceivedTime;

public interface MessageGroupReceivedTimeRepository extends JpaRepository<MessageGroupReceivedTime, Integer> {

    @Query("SELECT m FROM MessageGroupReceivedTime m WHERE m.messageGroup.id = :messageGroupId AND m.receiver.id = :receiverId")
    Optional<MessageGroupReceivedTime> getMessageGroupReceivedTimeByMessageGroupIdAndReceiverId(Integer messageGroupId,
            Integer receiverId);
}
