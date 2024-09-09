package com.example.chat.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.chat.chat.model.Channel;
import com.example.chat.chat.model.Queue;
import com.example.chat.registration.model.LocalUser;

import jakarta.transaction.Transactional;

@SuppressWarnings("null")
public interface ChannelRepository extends JpaRepository<Channel, Integer> {

  Optional<Channel> findByName(String name);

 
  Optional<Channel> findById(Integer id);

  @Query("SELECT COUNT(u) > 0 FROM Channel c JOIN c.followers u WHERE c.id = :channelId AND u.id = :userId")
  boolean isUserInChannel(@Param("channelId") Integer channelId, @Param("userId") Integer userId);

  @Transactional
  @Modifying
  @Query(value = "INSERT INTO channel_follower (channel_id, user_id) VALUES (:channelId, :userId)", nativeQuery = true)
  void addFollower(@Param("channelId") Integer channelId, @Param("userId") Integer userId);

  @Query("SELECT c.followers FROM Channel c WHERE c.id = :id")
  List<LocalUser> getFollowersByChannelId(Integer id);

  @Query("SELECT c FROM Channel c WHERE c.owner.id = :ownerId")
  List<Channel> getChannelsByOwnerId(Integer ownerId);

  @Query("SELECT c.owner FROM Channel c WHERE c.id = :id")
  Optional<LocalUser> getOwnerByChannelId(Integer id);

  @Query("SELECT c.queue FROM Channel c WHERE c.id = :id")
  Optional<Queue> getQueueByChannelId(Integer id);

  @Query("SELECT c FROM Channel c JOIN c.followers u WHERE u.id = :userId ORDER BY c.lastUpdated DESC")
  List<Channel> getFollowedChannelsByUserId(@Param("userId") Integer userId);

@Transactional
@Modifying
@Query(value = "DELETE FROM channel_follower  WHERE channel_id = :channelId", nativeQuery = true)
void deleteChannelFromFollowers(Integer channelId);
    
}
