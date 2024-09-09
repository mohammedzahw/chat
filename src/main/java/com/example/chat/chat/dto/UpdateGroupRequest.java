package com.example.chat.chat.dto;

import com.example.chat.chat.model.GroupPrivacy;

import lombok.Data;

@Data
public class UpdateGroupRequest {
    private Integer id;
    private String name;
    private String description;
    private GroupPrivacy privacy;
}
