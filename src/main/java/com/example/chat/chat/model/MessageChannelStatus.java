package com.example.chat.chat.model;

import com.example.chat.registration.model.LocalUser;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class MessageChannelStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private MessageStatus status;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private LocalUser receiver;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private MessageChannel messageChannel;
}
