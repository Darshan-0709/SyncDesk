package com.SyncDesk.dto.task;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateTaskDTO {

    @NotEmpty(message = "Task name is required.")
    private String name;

    private String description;

    private Long assignedTo;

    @NotEmpty(message = "Task priority is required.")
    @Pattern(regexp = "LOW|MEDIUM|HIGH", message = "Priority must be LOW, MEDIUM, or HIGH.")
    private String priority;

    @Pattern(regexp = "TO_DO|IN_PROGRESS|COMPLETED|BLOCKED",
            message = "Status must be PENDING, IN_PROGRESS, COMPLETED, or BLOCKED.")
    private String status;

    @NotNull(message = "Due date is required.")
    private LocalDate dueDate;
}