package com.SyncDesk.repository;

import com.SyncDesk.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    // Define any additional query methods if necessary
    List<ProjectMember> findAllByProjectId(Long id);

    Optional<ProjectMember> findByUserId(Long id);

    boolean existsByProjectIdAndUserId(Long project, Long memberToAdd);

    Optional<ProjectMember> findByUserIdAndProjectId(Long id, Long projectId);
}

