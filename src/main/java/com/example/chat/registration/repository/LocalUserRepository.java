package com.example.chat.registration.repository;

import java.nio.channels.Channel;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.chat.chat.model.Chat;
import com.example.chat.chat.model.Queue;
import com.example.chat.registration.model.LocalUser;

import jakarta.transaction.Transactional;

public interface LocalUserRepository extends JpaRepository<LocalUser, Integer> {

    Optional<LocalUser> findByEmail(String email);

    Optional<LocalUser> findByEmailAndPassword(String email, String password);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO local_user_listend_queues (user_id, queue_id) VALUES (:userId, :queueId)", nativeQuery = true)
    void addQueue(Integer userId, Integer queueId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_chat (user_id, chat_id) VALUES (:userId, :chatId)", nativeQuery = true)
    void addChat(Integer userId, Integer chatId);

    @Query("SELECT u.listendQueues FROM LocalUser u WHERE u.id = :userId")
    List<Queue> getQueuesByUserId(Integer userId);

    @Query("SELECT u.chats FROM LocalUser u WHERE u.id = :userId")
    List<Chat> getChatsByUserId(Integer userId);

    @Query("SELECT u.groups FROM LocalUser u WHERE u.id = :userId")
    List<Group> getGroupsByUserId(Integer userId);

    @Query("SELECT u.channels FROM LocalUser u WHERE u.id = :userId")
    List<Channel> getChannelsByUserId(Integer userId);

}
