package com.example.chat.registration.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SignUpRequestDto {

    @Pattern(regexp = "^\\S*$", message = "Spaces are not allowed")
    private String name;

    @Email(message = "Invalid email address")
    @NotEmpty(message = "Email cannot be empty")
    private String email;

    @NotEmpty(message = "Password cannot be empty")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$", 
    message = "Password must contain at least one number, one uppercase letter, one lowercase letter, and be at least 8 characters long")

    private String password;
    @Pattern(regexp = "^01\\d{9}$", message = "Invalid phone number")
    private String phone;

    private String about;

}
