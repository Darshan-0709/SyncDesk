package com.SyncDesk.service;

import com.SyncDesk.dto.project.CreateProjectDTO;
import com.SyncDesk.dto.project.ProjectDTO;
import com.SyncDesk.dto.project.UpdateProjectDTO;
import com.SyncDesk.entity.Project;
import com.SyncDesk.entity.ProjectMember;
import com.SyncDesk.entity.Role;
import com.SyncDesk.entity.User;
import com.SyncDesk.repository.ProjectMemberRepository;
import com.SyncDesk.repository.ProjectRepository;
import com.SyncDesk.repository.RoleRepository;
import com.SyncDesk.repository.UserRepository;
import com.SyncDesk.utils.DTOConverter;
import com.SyncDesk.utils.NoProjectFoundException;
import com.SyncDesk.utils.NoSuchUserFoundException;
import com.SyncDesk.utils.ProjectAlreadyExistsException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.SyncDesk.utils.DTOConverter.convertToProjectDTO;

@Service
public class ProjectServiceImpl implements ProjectService{

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final RoleRepository roleRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository, ProjectRepository projectMemberRepository, ProjectMemberRepository projectMemberRepository1, RoleRepository roleRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository1;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public ProjectDTO createProject(CreateProjectDTO createProjectDTO) throws ProjectAlreadyExistsException {
        if(projectRepository.existsByName(createProjectDTO.getName())) {
            throw new ProjectAlreadyExistsException("Project already exists");
        }

        Project project = new Project();
        project.setName(createProjectDTO.getName());

        if (createProjectDTO.getDescription() != null && !createProjectDTO.getDescription().isEmpty()) {
            project.setDescription(createProjectDTO.getDescription());
        }

        project.setStartDate(createProjectDTO.getStartDate());
        if (createProjectDTO.getEndDate() != null) {
            project.setEndDate(createProjectDTO.getEndDate());
        }

        // Retrieve current user's email from SecurityContext
        String currentUserEmail = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            currentUserEmail = ((UserDetails) principal).getUsername();
        } else {
            currentUserEmail = principal.toString();
        }

        // Retrieve User entity based on current user's email
        User user = userRepository
                .findByEmail(currentUserEmail)
                .orElseThrow(() -> new NoSuchUserFoundException("User not found"));

        // Set user as the project admin
        project.setUser(user);
        project = projectRepository.save(project);

        // Additional logic to add the user as a ProjectMember with the ADMIN role
        Role adminRole = roleRepository
                .findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

        ProjectMember projectMember = new ProjectMember();
        projectMember.setUser(user);
        projectMember.setProject(project);
        projectMember.setRole(adminRole);
        projectMember.setJoinedAt(LocalDate.now());
        projectMemberRepository.save(projectMember);

        return convertToProjectDTO(project);
    }

    @Override
    public ProjectDTO getById(Long id) throws NoProjectFoundException {
        Project project = projectRepository
                .findById(id)
                .orElseThrow(() -> new NoProjectFoundException("Unable to fetch project"));
        return convertToProjectDTO(project);
    }

    @Override
    public ProjectDTO updateProject(Long id, UpdateProjectDTO updateProjectDTO) throws NoProjectFoundException {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NoProjectFoundException("No such project found"));
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

    public boolean existsById(Long id){
        return projectRepository.existsById(id);
    }
}
