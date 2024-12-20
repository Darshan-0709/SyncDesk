package com.SyncDesk.repository;

import com.SyncDesk.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    // Define any additional query methods if necessary
    List<ProjectMember> findAllByProjectId(Long id);

    Optional<ProjectMember> findByUserId(Long id);

    boolean existsByProjectIdAndUserId(Long project, Long memberToAdd);
    Optional<ProjectMember> findByUserIdAndProjectId(Long userId, Long projectId);
    Optional<ProjectMember> findByIdAndProjectId(Long id, Long projectId);
    @Query("SELECT pm FROM ProjectMember pm " +
            "JOIN FETCH pm.project p " +
            "JOIN FETCH pm.role r " +
            "WHERE pm.user.id = :userId")
    List<ProjectMember> findAllByUserIdWithProjectsAndRoles(@Param("userId") Long userId);
}

