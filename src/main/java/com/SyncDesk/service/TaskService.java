package com.SyncDesk.service;

import com.SyncDesk.dto.task.CreateTaskDTO;
import com.SyncDesk.dto.task.TaskResponseDTO;
import com.SyncDesk.utils.ResourceNotFoundException;
import jakarta.validation.Valid;

import java.util.List;

public interface TaskService {
    List<TaskResponseDTO> findAllByProjectId(Long id);
    TaskResponseDTO getTaskById(Long projectId, Long taskId) throws ResourceNotFoundException;
    TaskResponseDTO createTask(Long project_id, CreateTaskDTO createTaskDTO) throws ResourceNotFoundException;

    TaskResponseDTO updateTask(Long projectId, Long taskId, @Valid CreateTaskDTO updateTaskDTO) throws ResourceNotFoundException;

    void deleteTask(Long projectId, Long taskId) throws ResourceNotFoundException;
}
