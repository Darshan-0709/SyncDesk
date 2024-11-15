package com.SyncDesk.dto.project_member;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AddMemberDTO {
    @NotEmpty(message = "Email is required")
    private String email;

    @NotEmpty(message = "Role is required")
    private String role;
}
