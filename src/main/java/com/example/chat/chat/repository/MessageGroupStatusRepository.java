package com.example.chat.chat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.chat.chat.model.MessageGroupStatus;

public interface MessageGroupStatusRepository extends JpaRepository<MessageGroupStatus, Integer> {
    // get message status by groupid and userID
    @Query("SELECT mgs FROM MessageGroupStatus mgs WHERE mgs.messageGroup.group.id = :groupId AND mgs.receiver.id = :receiverId")
    Optional<MessageGroupStatus> getMessageGroupStatusByReceiverId(@Param("groupId") Integer groupId,
            @Param("receiverId") Integer receiverId);

    @Query("SELECT count(mgs) FROM MessageGroupStatus mgs WHERE mgs.messageGroup.group.id = :groupId AND mgs.status = 'SENT'")
    Long getNumberOfUnreadMessage(@Param("groupId") Integer groupId);



    // set status
    // @Query("UPDATE MessageGroupStatus mgs SET mgs.status = :status WHERE mgs.id =
    // :id")
    // void setStatus(@Param("id") Integer id, @Param("status") MessageStatus
    // status);

}
