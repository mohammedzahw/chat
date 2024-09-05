package com.example.chat.registration.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateUserRequestDto {
    @Pattern(regexp = "^\\S*$", message = "Spaces are not allowed")
    private String name;

    @Email(message = "Invalid email address")
    @NotEmpty(message = "Email cannot be empty")
    private String email;

    @Pattern(regexp = "^01\\d{9}$", message = "Invalid phone number")
    private String phoneNumber;
}
