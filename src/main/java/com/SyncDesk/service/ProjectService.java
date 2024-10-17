package com.SyncDesk.service;

import com.SyncDesk.dto.project.CreateProjectDTO;
import com.SyncDesk.dto.project.ProjectDTO;
import com.SyncDesk.dto.project.UpdateProjectDTO;

import java.util.List;

public interface ProjectService {
    ProjectDTO createProject(CreateProjectDTO createProjectDTO);
    ProjectDTO updateProject(Long id, UpdateProjectDTO updateProjectDTO);
    boolean deleteProject(Long id);
    List<ProjectDTO> getAllProject();
    ProjectDTO getById(Long id);
}
