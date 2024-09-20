package com.example.chat.chat.model;

import java.time.LocalDateTime;
import java.util.List;

import com.example.chat.registration.model.LocalUser;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class MessageChat {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Integer id;
        private String content;
        
    private String urlId;
        private Double size;
        private Double duration;
        @Enumerated(EnumType.STRING)
        private MessageType type;
        private LocalDateTime sendDateTime;
        private LocalDateTime receiveDateTime;
        @Enumerated(EnumType.STRING)
        private MessageStatus status;

        @ManyToOne(fetch = FetchType.EAGER, cascade = {
                        CascadeType.PERSIST,
                        CascadeType.MERGE,
                        CascadeType.DETACH,
                        CascadeType.REFRESH,
                      
        })
        // @ToStringExclude
        // @JsonIgnore
        @JoinColumn(name = "parent_message_id")
        private MessageChat parentMessage;

        @OneToMany(mappedBy = "parentMessage", fetch = FetchType.LAZY, cascade = {
                        CascadeType.PERSIST,
                        CascadeType.MERGE,
                        CascadeType.DETACH,
                        CascadeType.REFRESH,
                        CascadeType.REMOVE

        })
        @JsonIgnore
        @ToString.Exclude
        private List<MessageChat> childMessages;

        @ManyToOne(fetch = FetchType.EAGER, cascade = {
                        CascadeType.PERSIST,
                        CascadeType.MERGE,
                        CascadeType.DETACH,
                        CascadeType.REFRESH

        })

        // @JsonIgnore
        @JoinColumn(name = "sender_id")
        private LocalUser sender;

        @ManyToOne(fetch = FetchType.LAZY, cascade = {
                        CascadeType.PERSIST,
                        CascadeType.MERGE,
                        CascadeType.DETACH,
                        CascadeType.REFRESH

        })
        @ToString.Exclude
        @JsonIgnore
        @JoinColumn(name = "chat_id")
        private Chat chat;

        @ManyToMany(mappedBy = "messageChat", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
        @ToString.Exclude
        @JsonIgnore
        private List<MessageChatReaction> reactions;

}
