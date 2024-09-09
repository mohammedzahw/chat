package com.example.chat.chat.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.chat.chat.model.MessageGroup;
import com.example.chat.chat.model.MessageGroupReaction;

public interface MessageGroupRepository extends JpaRepository<MessageGroup, Integer> {

    // get last message
    @Query("SELECT m FROM MessageGroup m WHERE m.group.id = :groupId ORDER BY m.sendDateTime DESC")
    List<MessageGroup> getLastMessageByGroupId(Integer groupId, Pageable pageable);
    // get number of unread message


    @Query("SELECT m FROM MessageGroupReaction m WHERE m.messageGroup.id = :messageGroupId")
    List<MessageGroupReaction> getMessageGroupReactionsByMessageGroupId(Integer messageGroupId);




    @Query("SELECT m FROM MessageGroup m WHERE m.group.id = :id ORDER BY m.sendDateTime DESC")
    List<MessageGroup> getGroupMessages(Integer id, Pageable pageable);

}

