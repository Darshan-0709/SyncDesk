package com.SyncDesk.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserRegistrationDTO {

    @NotEmpty(message = "Name is required")
    private String fullName;

    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid Email")
    private String email;

    @NotEmpty(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 character long")
    private String password;
    @NotEmpty(message = "Confirmed Password is required")
    private String confirmedPassword;
}
