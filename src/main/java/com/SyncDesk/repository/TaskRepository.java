package com.SyncDesk.repository;

import com.SyncDesk.dto.task.CreateTaskDTO;
import com.SyncDesk.dto.task.TaskResponseDTO;
import com.SyncDesk.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    void deleteByIdAndProjectId(Long taskId, Long projectId);
    Optional<Task> findByIdAndProjectId(Long taskId, Long projectId);
    List<Task> findAllByProjectId(Long projectId);
}
