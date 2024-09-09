package com.example.chat.chat.model;

import lombok.Getter;

@Getter
public enum MessageStatus {
    SENT,
    RECEIVED,
    READ;
}