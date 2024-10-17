package com.SyncDesk.service;

import com.SyncDesk.dto.project.CreateProjectDTO;
import com.SyncDesk.dto.project.ProjectDTO;
import com.SyncDesk.dto.project.UpdateProjectDTO;
import com.SyncDesk.entity.Project;
import com.SyncDesk.entity.User;
import com.SyncDesk.repository.ProjectRepository;
import com.SyncDesk.repository.UserRepository;
import com.SyncDesk.utils.DTOConverter;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.SyncDesk.utils.DTOConverter.convertToProjectDTO;

@Service
public class ProjectServiceImpl implements ProjectService{

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ProjectDTO createProject(CreateProjectDTO createProjectDTO) {
        if(projectRepository.existsByName(createProjectDTO.getName())){
            throw new ProjectAlreadyExists("Project already Exists");
        }
        Project project = new Project();
        project.setName(createProjectDTO.getName());
        if(!createProjectDTO.getDescription().isEmpty()){
            project.setDescription(createProjectDTO.getDescription());
        }
        project.setStartDate(createProjectDTO.getStartDate());
        if(createProjectDTO.getEndDate() != null){
            project.setEndDate(createProjectDTO.getEndDate());
        }
        User user = userRepository
                .findById(1L)
                .orElseThrow(() -> new NoSuchUserFoundException("Unauthorized access"));

        project.setUser(user);

        project = projectRepository.save(project);

        return convertToProjectDTO(project);
    }

    @Override
    public ProjectDTO getById(Long id) {
        Project project = projectRepository
                .findById(id)
                .orElseThrow(() -> new NoSuchProjectFound("Unable to fetch project"));
        return convertToProjectDTO(project);
    }

    @Override
    public ProjectDTO updateProject(Long id, UpdateProjectDTO updateProjectDTO) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NoSuchProjectFound("No such project found"));
        project.setName(updateProjectDTO.getName());
        if(!updateProjectDTO.getDescription().isEmpty()){
            project.setDescription(updateProjectDTO.getDescription());
        }
        project.setStartDate(updateProjectDTO.getStartDate());
        if(updateProjectDTO.getEndDate() != null){
            project.setEndDate(updateProjectDTO.getEndDate());
        }
        return convertToProjectDTO(project);
    }

    @Override
    public boolean deleteProject(Long id) {
        try{
            projectRepository.deleteById(id);
        }catch (Exception e){
            throw new RuntimeException("Unable to delete project");
        }
        return true;
    }

    @Override
    public List<ProjectDTO> getAllProject() {
        return projectRepository.findAll().stream().map(DTOConverter::convertToProjectDTO).toList();
    }


    private static class NoSuchProjectFound extends RuntimeException {
        public NoSuchProjectFound(String message) {
            super(message);
        }
    }

    private static class ProjectAlreadyExists extends RuntimeException {
        public ProjectAlreadyExists(String message) {
            super(message);
        }
    }
}
