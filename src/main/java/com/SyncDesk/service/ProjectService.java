package com.SyncDesk.service;

import com.SyncDesk.dto.project.CreateProjectDTO;
import com.SyncDesk.dto.project.ProjectDTO;
import com.SyncDesk.dto.project.UpdateProjectDTO;
import com.SyncDesk.utils.NoProjectFoundException;
import com.SyncDesk.utils.ProjectAlreadyExistsException;
import com.SyncDesk.utils.ResourceNotFoundException;

import java.util.List;

public interface ProjectService {
    ProjectDTO createProject(CreateProjectDTO createProjectDTO) throws ProjectAlreadyExistsException;
    ProjectDTO updateProject(Long id, UpdateProjectDTO updateProjectDTO) throws NoProjectFoundException, ResourceNotFoundException;
    boolean deleteProject(Long id);
//    List<ProjectDTO> getAllProject();
    ProjectDTO getById(Long id) throws NoProjectFoundException;
}
