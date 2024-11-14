package com.SyncDesk.dto.project_member;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProjectMemberDTO {
    private Long userId;
    private Long projectId;
    private String roleName;
    private LocalDate joinedAt;
}
