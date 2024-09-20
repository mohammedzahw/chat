package com.example.chat.chat.model;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringExclude;

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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private GroupPrivacy privacy;
    private LocalDateTime lastUpdated;
    private LocalDateTime createdDate;

    @OneToOne(mappedBy = "group", fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @ToStringExclude
    @JsonIgnore
    private ImageGroup imageGroup;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "group_admin", joinColumns = @JoinColumn(name = "group_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    @ToStringExclude
    @JsonIgnore
    private List<LocalUser> admins;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "group_member", joinColumns = @JoinColumn(name = "group_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    @ToStringExclude
    @JsonIgnore
    private List<LocalUser> members;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @ToStringExclude
    @JsonIgnore
    private LocalUser owner;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToStringExclude
    @JsonIgnore
    private List<MessageGroup> messages;
    
    @JoinColumn(name = "queue_id")
    @OneToOne(fetch = FetchType.EAGER, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.DETACH,
            CascadeType.REFRESH
    })
    @ToStringExclude
    @JsonIgnore
    private Queue queue;

    public Group(
            Integer id,
            String name,
            String description,
            GroupPrivacy privacy,
            LocalDateTime lastUpdated

    ) {
        this.id = id;
        this.name = name;
        this.description = description;

        this.privacy = privacy;
        this.lastUpdated = lastUpdated;
    }

}
