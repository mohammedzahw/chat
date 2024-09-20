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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class MessageGroup {
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
    @ManyToOne(fetch = FetchType.EAGER, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.DETACH,
            CascadeType.REFRESH

    })

    @JoinColumn(name = "sender_id")
    @JsonIgnore
    private LocalUser sender;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE,
                    CascadeType.DETACH,
                    CascadeType.REFRESH
    })
    @JoinColumn(name = "parent_message_id")

    private MessageGroup parentMessage;

    @OneToMany(mappedBy = "parentMessage", fetch = FetchType.LAZY, cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE,
                    CascadeType.DETACH,
                    CascadeType.REFRESH

    })
    @JsonIgnore
    private List<MessageGroup> childMessages;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE,
                    CascadeType.DETACH,
                    CascadeType.REFRESH

    })
    @JoinColumn(name = "group_id")
    @JsonIgnore
    private Group group;

    @ManyToMany(mappedBy = "messageGroup", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<MessageGroupReaction> reactions;

    @ManyToMany(mappedBy = "messageGroup", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MessageGroupStatus> statuses;

    @ManyToMany(mappedBy = "messageGroup", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MessageGroupReceivedTime> receivedTimes;

    public MessageGroup(
            String content,
            String urlId,
            Double size,
            Double duration,
            MessageType type,
            LocalDateTime sendDateTime,
            LocalUser sender,
            Group group,
            MessageGroup parentMessage
    ) {
        this.content = content;
        this.urlId = urlId;
        this.size = size;
        this.duration = duration;
        this.type = type;
        this.sendDateTime = sendDateTime;
        this.sender = sender;
        this.group = group;
        this.parentMessage = parentMessage;
    }

}
