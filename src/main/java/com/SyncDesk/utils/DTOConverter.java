package com.SyncDesk.utils;

import com.SyncDesk.dto.project.ProjectDTO;
import com.SyncDesk.dto.project_member.ProjectMemberDTO;
import com.SyncDesk.dto.task.TaskResponseDTO;
import com.SyncDesk.dto.user.UserDTO;
import com.SyncDesk.entity.Project;
import com.SyncDesk.entity.ProjectMember;
import com.SyncDesk.entity.Task;
import com.SyncDesk.entity.User;

import java.util.List;
import java.util.stream.Collectors;

public class DTOConverter {

    public static UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFullName(user.getFullName());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }

    public static ProjectDTO convertToProjectDTO(Project project, Long currentUserId, String currentUserRole) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(project.getId());
        projectDTO.setName(project.getName());

        if (project.getDescription() != null && !project.getDescription().isEmpty()) {
            projectDTO.setDescription(project.getDescription());
        }

        // Use the static UserDTO converter for the admin
        UserDTO userDTO = convertToUserDTO(project.getUser());
        projectDTO.setAdmin(userDTO);

        projectDTO.setStartDate(project.getStartDate());

        if (project.getEndDate() != null) {
            projectDTO.setEndDate(project.getEndDate());
        }

        // Convert project members to ProjectMemberDTOs
//        if (project.getProjectMembers() != null) {
//            List<ProjectMemberDTO> memberDTOs = project.getProjectMembers().stream().map(member -> {
//                ProjectMemberDTO memberDTO = new ProjectMemberDTO();
//                memberDTO.setId(member.getId());
//                memberDTO.setUser(convertToUserDTO(member.getUser())); // Convert User to UserDTO
//                memberDTO.setRoleName(member.getRole().getName()); // Set role name
//                memberDTO.setProjectId(member.getProject().getId());
//                memberDTO.setJoinedAt(member.getJoinedAt());
//                return memberDTO;
//            }).collect(Collectors.toList());
//            projectDTO.setMembers(memberDTOs);
//        }

        // Set the role of the current user in this project
//        String currentUserRole = project.getProjectMembers().stream()
//                .filter(member -> member.getUser().getId().equals(currentUserId)) // Match the current user
//                .map(member -> member.getRole().getName()) // Extract role name
//                .findFirst()
//                .orElse(null); // Handle cases where user isn't a member
        projectDTO.setUserRole(currentUserRole);
//        String currentUserRole = project

        return projectDTO;
    }


    public static ProjectMemberDTO convertToProjectMemberDTO(ProjectMember projectMember) {
        ProjectMemberDTO projectMemberDTO = new ProjectMemberDTO();
        projectMemberDTO.setId(projectMember.getId());
        projectMemberDTO.setUser(convertToUserDTO(projectMember.getUser()));
        projectMemberDTO.setProjectId(projectMember.getProject().getId());
        projectMemberDTO.setRoleName(projectMember.getRole().getName());
        projectMemberDTO.setJoinedAt(projectMember.getJoinedAt());
        return projectMemberDTO;
    }

    public static TaskResponseDTO convertToTaskResponseDTO(Task task) {
        return TaskResponseDTO.builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription() != null ? task.getDescription() : null)
                .priority(task.getPriority() != null ? task.getPriority().toString() : null)
                .status(task.getStatus() != null ? task.getStatus().toString() : null)
                .dueDate(task.getDueDate())
                .createdBy(task.getCreatedBy() != null ? convertToUserDTO(task.getCreatedBy().getUser()) : null)
                .assignedTo(task.getAssignedTo() != null ? convertToUserDTO(task.getAssignedTo().getUser()) : null)
                .projectId(task.getProject().getId())
                .build();
    }


}
