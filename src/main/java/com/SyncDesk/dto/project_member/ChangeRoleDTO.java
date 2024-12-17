package com.SyncDesk.dto.project_member;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ChangeRoleDTO {
    @NotNull(message = "Id is required")
    private Long id;
    @NotEmpty(message = "Role is required")
    private String role;
}
