package com.example.chat.chat.model;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringExclude;

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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class MessageChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String urlId;
    private String content;
    private Double size;
    private Double duration;
    @Enumerated(EnumType.STRING)
    private MessageType type;
    private LocalDateTime sendDateTime;
    @ManyToOne(fetch = FetchType.EAGER, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.DETACH,
            CascadeType.REFRESH
    })
    @ToStringExclude
    @JoinColumn(name = "parent_message_id")
    // @JsonIgnore
    private MessageChannel parentMessage;

    @OneToMany(mappedBy = "parentMessage", fetch = FetchType.LAZY, cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE,
                    CascadeType.DETACH,
                    CascadeType.REFRESH
    })
    @ToStringExclude
    @JsonIgnore
    private List<MessageChannel> childMessages;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.DETACH,
            CascadeType.REFRESH

    })
    @ToStringExclude
    @JoinColumn(name = "channel_id")
    // @JsonIgnore
    private Channel channel;

    @ManyToMany(mappedBy = "messageChannel", fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JsonIgnore
    private List<MessageChannelReaction> reactions;
}
