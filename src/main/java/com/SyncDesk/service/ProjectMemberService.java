package com.SyncDesk.service;

import com.SyncDesk.dto.project_member.AddMemberDTO;
import com.SyncDesk.dto.project_member.ChangeRoleDTO;
import com.SyncDesk.dto.project_member.ProjectMemberDTO;
import com.SyncDesk.entity.ProjectMember;
import com.SyncDesk.utils.NoProjectFoundException;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberService {
    List<ProjectMemberDTO> getAllByProjectId(Long id) throws NoProjectFoundException;
    ProjectMemberDTO addMember(Long id, AddMemberDTO member);
    ProjectMemberDTO editMember(Long id, ChangeRoleDTO changeRoleDTO);
}
