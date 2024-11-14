package com.SyncDesk.service;

import com.SyncDesk.dto.project_member.ProjectMemberDTO;
import com.SyncDesk.dto.user.UserDTO;
import com.SyncDesk.repository.ProjectMemberRepository;

import java.util.List;
import java.util.Optional;

public class ProjectMemberServiceImpl implements ProjectMemberService{

    private final ProjectMemberRepository projectMemberRepository;

    public ProjectMemberServiceImpl(ProjectMemberRepository projectMemberRepository) {
        this.projectMemberRepository = projectMemberRepository;
    }

    @Override
    public List<ProjectMemberDTO> fetchAllMembers(Long id) {
        //get the all member where project id == id
        return null;
    }
}
