package com.SyncDesk.dto.project;

import com.SyncDesk.dto.project_member.ProjectMemberDTO;
import com.SyncDesk.dto.user.UserDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ProjectDTO {
    private Long id;
    private String name;
    private UserDTO admin;
    private String userRole;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
//    private List<ProjectMemberDTO> members;
}

