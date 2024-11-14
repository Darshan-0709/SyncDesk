package com.SyncDesk.controller;

import com.SyncDesk.dto.project.CreateProjectDTO;
import com.SyncDesk.dto.project.ProjectDTO;
import com.SyncDesk.dto.project.UpdateProjectDTO;
import com.SyncDesk.service.ProjectServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
public class ProjectController {

    private final ProjectServiceImpl projectService;

    public ProjectController(ProjectServiceImpl projectService) {
        this.projectService = projectService;
    }

    @GetMapping()
    public ResponseEntity<?> getAllProject(){
        List<ProjectDTO> projects = projectService.getAllProject();
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        try{
            ProjectDTO projectDTO = projectService.getById(id);
            return new ResponseEntity<>(projectDTO, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping()
    public ResponseEntity<?> createProject(@Valid @RequestBody CreateProjectDTO createProjectDTO){
        try{
            ProjectDTO projectDTO = projectService.createProject(createProjectDTO);
            return new ResponseEntity<>(projectDTO, HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@Valid @PathVariable Long id, @RequestBody UpdateProjectDTO updateProjectDTO){
        try{
            ProjectDTO projectDTO = projectService.updateProject(id, updateProjectDTO);
            return new ResponseEntity<>(projectDTO, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable Long id){
        try{
            boolean isDeleted = projectService.deleteProject(id);
            return new ResponseEntity<>("Project deleted successfully", HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
