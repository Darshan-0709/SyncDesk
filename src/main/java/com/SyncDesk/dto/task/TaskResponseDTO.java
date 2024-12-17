package com.SyncDesk.dto.task;

import com.SyncDesk.dto.user.UserDTO;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class TaskResponseDTO {

    private Long id;
    private String name;
    private String description;
    private UserDTO createdBy;
    private UserDTO assignedTo;
    private String priority;
    private String status;
    private LocalDate dueDate;
    private Long projectId;
}
