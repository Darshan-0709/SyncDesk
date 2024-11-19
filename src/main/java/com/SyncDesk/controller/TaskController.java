package com.SyncDesk.controller;


import com.SyncDesk.common.ApiResponse;
import com.SyncDesk.dto.task.CreateTaskDTO;
import com.SyncDesk.dto.task.TaskResponseDTO;
import com.SyncDesk.service.ProjectServiceImpl;
import com.SyncDesk.service.TaskServiceImpl;
import com.SyncDesk.utils.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("project/{projectId}/task")
public class TaskController {

    private final TaskServiceImpl taskService;
    private final ProjectServiceImpl projectService;

    public TaskController(TaskServiceImpl taskService, ProjectServiceImpl projectService) {
        this.taskService = taskService;
        this.projectService = projectService;
    }


    @GetMapping()
    public ResponseEntity<ApiResponse<List<TaskResponseDTO>>> fetchAllTask(
            @PathVariable("projectId") Long projectId) throws ResourceNotFoundException {
        if (!projectService.existsById(projectId)) {
            throw new ResourceNotFoundException("Project with ID " + projectId + " not found.");
        }
        List<TaskResponseDTO> tasks = taskService.findAllByProjectId(projectId);
        return ResponseEntity.ok(new ApiResponse<>("Tasks fetched successfully", tasks));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> fetchTask(
            @PathVariable("projectId") Long projectId,
            @PathVariable("taskId") Long taskId) throws ResourceNotFoundException {
        TaskResponseDTO task = taskService.getTaskById(projectId, taskId);
        return ResponseEntity.ok(new ApiResponse<>("Task fetched successfully", task));
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<TaskResponseDTO>> createTask(
            @PathVariable("projectId") Long projectId,
            @Valid @RequestBody CreateTaskDTO createTaskDTO) throws ResourceNotFoundException {
            TaskResponseDTO task = taskService.createTask(projectId, createTaskDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Task created successfully", task));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> updateTask(
            @PathVariable("projectId") Long projectId,
            @PathVariable("taskId") Long taskId,
            @Valid @RequestBody CreateTaskDTO updateTaskDTO) throws ResourceNotFoundException {
        TaskResponseDTO updatedTask = taskService.updateTask(projectId, taskId, updateTaskDTO);
        return ResponseEntity.ok(new ApiResponse<>("Task updated successfully", updatedTask));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @PathVariable("projectId") Long projectId,
            @PathVariable("taskId") Long taskId) throws ResourceNotFoundException {
        taskService.deleteTask(projectId, taskId);
        return ResponseEntity.ok(new ApiResponse<>("Task deleted successfully", null));
    }

}

