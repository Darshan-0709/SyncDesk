package com.SyncDesk.dto.project;

import com.SyncDesk.dto.user.UserDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProjectDTO {
    public Long id;
    public String name;
    public UserDTO user;
    public String description;
    public LocalDate startDate;
    public LocalDate endDate;
}
