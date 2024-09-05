package com.example.chat.registration.model;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringExclude;

import com.example.chat.chat.model.Channel;
import com.example.chat.chat.model.Chat;
import com.example.chat.chat.model.Group;
import com.example.chat.chat.model.Queue;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocalUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String about;
    private Boolean active;
    private String imageUrl;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "queue_id"))
    @ToStringExclude
    @JsonIgnore
    private List<Queue> listendQueues;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "followers")
    @ToStringExclude
    @JsonIgnore
    private List<Channel> channels;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "members")
    @ToStringExclude
    @JsonIgnore
    private List<Group> groups;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_chat", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "chat_id"))
    @ToStringExclude
    @JsonIgnore
    private List<Chat> chats;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.DETACH,
            CascadeType.REFRESH
    })
    @ToStringExclude
    @JsonIgnore
    private List<Role> roles;


}
