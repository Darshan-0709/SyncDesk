package com.SyncDesk.controller;

import com.SyncDesk.dto.project.ProjectDTO;
import com.SyncDesk.dto.project_member.ProjectMemberDTO;
import com.SyncDesk.service.ProjectMemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/members")
public class ProjectMemberController {

    public final ProjectMemberService projectMemberService;

    public ProjectMemberController(ProjectMemberService projectMemberService) {
        this.projectMemberService = projectMemberService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getAllMembers(@PathVariable Long id){
        List<ProjectMemberDTO> projectMembers =projectMemberService.getAllMembers(id);
        return new ResponseEntity<>(projectMembers, HttpStatus.OK);
    }
}
