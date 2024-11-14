package com.SyncDesk.repository;

import com.SyncDesk.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    // Define any additional query methods if necessary
}

