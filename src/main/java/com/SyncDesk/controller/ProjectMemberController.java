package com.SyncDesk.controller;

import com.SyncDesk.common.ApiResponse;
import com.SyncDesk.dto.project.ProjectDTO;
import com.SyncDesk.dto.project_member.AddMemberDTO;
import com.SyncDesk.dto.project_member.ChangeRoleDTO;
import com.SyncDesk.dto.project_member.ProjectMemberDTO;
import com.SyncDesk.service.ProjectMemberService;
import com.SyncDesk.service.ProjectMemberServiceImpl;
import com.SyncDesk.utils.NoProjectFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class ProjectMemberController {

    public final ProjectMemberServiceImpl projectMemberService;

    public ProjectMemberController(ProjectMemberServiceImpl projectMemberService) {
        this.projectMemberService = projectMemberService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<List<ProjectMemberDTO>>> getAllMembers(@PathVariable Long id) {
        try {
            List<ProjectMemberDTO> projectMembersDTO = projectMemberService.getAllByProjectId(id);
            return ResponseEntity.ok(new ApiResponse<>("Members fetched successfully", projectMembersDTO));
        } catch (NoProjectFoundException | Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectMemberDTO>> addMember(@PathVariable Long id, @Valid @RequestBody AddMemberDTO addMemberDTO){
        try {
            ProjectMemberDTO projectMembersDTO = projectMemberService.addMember(id, addMemberDTO);
            return ResponseEntity.ok(new ApiResponse<>("New member added successfully",projectMembersDTO));
        } catch (Exception e) {
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectMemberDTO>> editMember(@PathVariable Long id, @RequestParam String newRole) {
        try {
            ProjectMemberDTO projectMemberDTO = projectMemberService.editMember(id, newRole);
            return ResponseEntity.ok(new ApiResponse<>("Role updated successfully", projectMemberDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

}
