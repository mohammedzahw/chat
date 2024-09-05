package com.example.chat.registration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocalUserDto {
    private Integer id;
    private String name;
    private String about;
    private Boolean active;
    private String imageUrl;

}
