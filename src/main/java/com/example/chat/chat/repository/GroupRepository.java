package com.example.chat.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.chat.chat.model.Group;
import com.example.chat.chat.model.MessageGroup;
import com.example.chat.chat.model.Queue;
import com.example.chat.registration.model.LocalUser;

import jakarta.transaction.Transactional;
@SuppressWarnings("null")
public interface GroupRepository extends JpaRepository<Group, Integer> {
    Optional<Group> findByName(String name);

    Optional<Group> findById(Integer id);

    @Query("SELECT g.members FROM Group g WHERE g.id = :id")
    List<LocalUser> getMembersByGroupId(Integer id);

    @Query("SELECT g.owner FROM Group g WHERE g.id = :id")
    Optional<LocalUser> getOwnerByGroupId(Integer id);

    @Query("SELECT g.admins FROM Group g WHERE g.id = :id")
    List<LocalUser> getAdminsByGroupId(Integer id);

    @Query("SELECT g.queue FROM Group g WHERE g.id = :id")
    Optional<Queue> getQueueByGroupId(Integer id);

    @Query("SELECT gm FROM Group g JOIN g.messages gm WHERE g.id = :groupId ORDER BY gm.sendDateTime DESC")
    List<MessageGroup> getMessagesByGroupId(@Param("groupId") Integer groupId, Pageable pageable);


    @Query("SELECT g FROM Group g JOIN g.members m WHERE m.id = :userId ORDER BY g.lastUpdated DESC")
    List<Group> getGroupsByMemberId(Integer userId);

    @Query("SELECT g FROM Group g WHERE g.owner.id = :id ORDER BY g.lastUpdated DESC")
    List<Group> getGroupsByOwnerId(Integer id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM group_member WHERE group_id = :groupId", nativeQuery = true) 
    void deleteGroupFromMembers(Integer groupId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM group_admin WHERE group_id = :groupId", nativeQuery = true)
    void deleteGroupFromAdmins(Integer groupId);

}
