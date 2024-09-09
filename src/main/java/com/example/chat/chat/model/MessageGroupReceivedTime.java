package com.example.chat.chat.model;

import java.time.LocalDateTime;

import com.example.chat.registration.model.LocalUser;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class MessageGroupReceivedTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private LocalDateTime time;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private LocalUser receiver;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private MessageGroup messageGroup;

}
