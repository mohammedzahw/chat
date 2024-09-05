package com.example.chat.registration.dto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LocalUserProfileDto {
    private String name;
    private String email;
    private String phone;
    private Boolean active;
    private String imageUrl;
    private String about;

}
