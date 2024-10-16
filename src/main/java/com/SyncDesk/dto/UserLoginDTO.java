package com.SyncDesk.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class UserLoginDTO {
    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid Email format")
    private String email;
    @NotEmpty(message = "Password is required")
    private String password;
}
