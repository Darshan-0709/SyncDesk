package com.SyncDesk.repository;

import com.SyncDesk.dto.project.ProjectDTO;
import com.SyncDesk.entity.Project;
import com.SyncDesk.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    boolean existsByName(String name);
    boolean existsById(Long id);
}
