package com.example.chat.chat.dto;

import lombok.Data;

@Data
public class UpdateChannelRequest {

    private Integer id;
    private String name;
    private String description;
    
}
