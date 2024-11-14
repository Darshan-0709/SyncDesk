package com.SyncDesk.service;

import com.SyncDesk.dto.project_member.ProjectMemberDTO;
import com.SyncDesk.dto.user.UserDTO;
import com.SyncDesk.entity.User;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberService {
    List<ProjectMemberDTO> fetchAllMembers(Long id);

}
