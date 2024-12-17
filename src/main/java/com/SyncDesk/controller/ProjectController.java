package com.SyncDesk.controller;

import com.SyncDesk.common.ApiResponse;
import com.SyncDesk.dto.project.CreateProjectDTO;
import com.SyncDesk.dto.project.ProjectDTO;
import com.SyncDesk.dto.project.UpdateProjectDTO;
import com.SyncDesk.service.ProjectServiceImpl;
import com.SyncDesk.utils.NoProjectFoundException;
import com.SyncDesk.utils.ProjectAlreadyExistsException;
import com.SyncDesk.utils.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
public class ProjectController {

    private final ProjectServiceImpl projectService;

    public ProjectController(ProjectServiceImpl projectService) {
        this.projectService = projectService;
    }


    @GetMapping()
    public ResponseEntity<List<ProjectDTO>> getProjectsForCurrentUser() {
        List<ProjectDTO> projects = projectService.getProjectsForCurrentUser();
        return ResponseEntity.ok(projects);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectDTO>> getById(@PathVariable Long id) {
        try {
            ProjectDTO projectDTO = projectService.getById(id);
            return ResponseEntity.ok(new ApiResponse<>("Project fetched successfully", projectDTO));
        } catch (NoProjectFoundException | Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<ProjectDTO>> createProject(@Valid @RequestBody CreateProjectDTO createProjectDTO) {
        try {
            ProjectDTO projectDTO = projectService.createProject(createProjectDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("Project created successfully", projectDTO));
        } catch (ProjectAlreadyExistsException | Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectDTO>> updateProject(@Valid @PathVariable Long id, @RequestBody UpdateProjectDTO updateProjectDTO) {
        try {
            ProjectDTO projectDTO = projectService.updateProject(id, updateProjectDTO);
            return ResponseEntity.ok(new ApiResponse<>("Project updated successfully", projectDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(e.getMessage(), null));
        } catch (ResourceNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProject(@PathVariable Long id) {
        try {
            boolean isDeleted = projectService.deleteProject(id);
            return ResponseEntity.ok(new ApiResponse<>("Project deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

}
