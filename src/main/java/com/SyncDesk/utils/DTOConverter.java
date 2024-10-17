package com.SyncDesk.utils;

import com.SyncDesk.dto.project.ProjectDTO;
import com.SyncDesk.dto.user.UserDTO;
import com.SyncDesk.entity.Project;
import com.SyncDesk.entity.User;

public class DTOConverter {

    public static UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFullName(user.getFullName());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }

    public static ProjectDTO convertToProjectDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(project.getId());
        projectDTO.setName(project.getName());

        if (project.getDescription() != null && !project.getDescription().isEmpty()) {
            projectDTO.setDescription(project.getDescription());
        }

        // Use the static UserDTO converter
        UserDTO userDTO = convertToUserDTO(project.getUser());
        projectDTO.setUser(userDTO);

        projectDTO.setStartDate(project.getStartDate());

        if (project.getEndDate() != null) {
            projectDTO.setEndDate(project.getEndDate());
        }

        return projectDTO;
    }


}
