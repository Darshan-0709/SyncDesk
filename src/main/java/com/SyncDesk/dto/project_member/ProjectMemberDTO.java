package com.SyncDesk.dto.project_member;

import com.SyncDesk.dto.user.UserDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProjectMemberDTO {
    private Long id;
    private UserDTO user;
    private Long projectId;
    private String roleName;
    private LocalDate joinedAt;
}
