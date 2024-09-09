package com.example.chat.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.chat.chat.model.MessageChannel;

public interface MessageChannelRepository extends JpaRepository<MessageChannel, Integer> {

    // get last message
    @Query("SELECT m FROM MessageChannel m WHERE m.channel.id = :channelId ORDER BY m.sendDateTime DESC")
    Optional<MessageChannel> getLastMessageByChannelId(Integer channelId);

    @Query("SELECT m FROM MessageChannel m WHERE m.channel.id = :id ORDER BY m.sendDateTime DESC")
    List<MessageChannel> getLastMessage(Integer id, Pageable pageable);

    @Query("SELECT m FROM MessageChannel m WHERE m.channel.id = :channelId ORDER BY m.sendDateTime DESC")
    List<MessageChannel> getMessagesByChannelId(Integer channelId, Pageable pageable);

    // @Query("SELECT m FROM MessageChannel m WHERE m.channel.id = :id AND m.isRead
    // = false")
    // Long getNumberOfUreadMessageByChannelId(Integer id);

}
