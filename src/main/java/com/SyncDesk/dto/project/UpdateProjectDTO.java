package com.SyncDesk.dto.project;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateProjectDTO {
    @NotNull(message = "No project associated")
    private Long id;
    @NotEmpty(message = "Project Name is required")
    private String name;
    private String description;
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    private LocalDate endDate;
}
