package com.example.chat.chat.model;

import org.apache.commons.lang3.builder.ToStringExclude;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class ImageChannel {
    @Id
    private String imageUrl;
    private String imageId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    @ToStringExclude
    @JsonIgnore
    private Channel channel;
}
