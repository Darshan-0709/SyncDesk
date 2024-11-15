package com.SyncDesk.dto.project_member;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class ChangeRoleDTO {
    @NotEmpty(message = "Role is required")
    private String role;
}
