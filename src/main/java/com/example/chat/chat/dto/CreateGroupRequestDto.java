package com.example.chat.chat.dto;

import com.example.chat.chat.model.GroupPrivacy;

import lombok.Data;

@Data
public class CreateGroupRequestDto {
    private String name;
    private String description;
    private GroupPrivacy privacy;
}
