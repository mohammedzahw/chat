package com.example.chat.chat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.chat.chat.model.Queue;

import jakarta.transaction.Transactional;

public interface QueueRepository extends JpaRepository<Queue, Integer> {

    Optional<Queue> findByName(String name);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM local_user_listend_queues WHERE queue_id = :id", nativeQuery = true)
    void deleteFromListenersById(Integer id);

}
