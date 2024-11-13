package com.SyncDesk.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponseDTO {
    private UserDTO user;
    private String token;

    public LoginResponseDTO(UserDTO user, String token) {
        this.user = user;
        this.token = token;
    }
}
