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
import com.SyncDesk.utils.*;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.SyncDesk.utils.DTOConverter.convertToProjectDTO;

@Service
public class ProjectServiceImpl implements ProjectService{

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final RoleRepository roleRepository;
    private final AuthService authService;

    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository, ProjectRepository projectMemberRepository, ProjectMemberRepository projectMemberRepository1, RoleRepository roleRepository, AuthService authService) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository1;
        this.roleRepository = roleRepository;
        this.authService = authService;
    }

    @Override
    @Transactional
    public ProjectDTO createProject(CreateProjectDTO createProjectDTO) throws ProjectAlreadyExistsException {
        User currentUser = authService.getCurrentUser();

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

        String currentUserRole = "ADMIN";
        return convertToProjectDTO(project, currentUser.getId(), currentUserRole);
    }

    @Override
    public ProjectDTO getById(Long id) throws NoProjectFoundException {
        User currentUser = authService.getCurrentUser();
        ProjectMember currentMember = projectMemberRepository.findByUserIdAndProjectId(currentUser.getId(), id)
                .orElseThrow(() -> new UnauthorizedAccessException("You are not authorized to view this project"));

        Project project = projectRepository
                .findById(id)
                .orElseThrow(() -> new NoProjectFoundException("Unable to fetch project"));
//
//        boolean isMember = project.getProjectMembers()
//                .stream()
//                .anyMatch(member -> member.getUser().getId().equals(currentUser.getId()));
//
//        if (!isMember) {
//            throw new UnauthorizedAccessException("You are not authorized to view this project");
//        }
        return convertToProjectDTO(project, currentUser.getId(), currentMember.getRole().getName());
    }

    public List<ProjectDTO> getProjectsForCurrentUser() {
        User currentUser = authService.getCurrentUser();
        List<ProjectMember> projectMemberships = projectMemberRepository.findAllByUserIdWithProjectsAndRoles(currentUser.getId());
//        ProjectMember currentMember = projectMemberRepository.findByUserIdAndProjectId(currentUser.getId(), id)
//                .orElseThrow(() -> new UnauthorizedAccessException("You are not authorized to view this project"));
        // Sort by role priority: Admin → Manager → User
        return projectMemberships.stream()
                .sorted(Comparator.comparingInt(member -> getRolePriority(member.getRole().getName())))
                .map(member -> DTOConverter.convertToProjectDTO(member.getProject(), currentUser.getId(), member.getRole().getName()))
                .collect(Collectors.toList());
    }


    private int getRolePriority(String roleName) {
        return switch (roleName.toLowerCase()) {
            case "admin" -> 1;
            case "manager" -> 2;
            case "user" -> 3;
            default -> 4; // Unknown roles have the lowest priority
        };
    }


    @Override
    public ProjectDTO updateProject(Long projectId, UpdateProjectDTO updateProjectDTO) throws ResourceNotFoundException {
        // Fetch the project by ID
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project with ID " + projectId + " not found."));

        // Fetch the current user
        User currentUser = authService.getCurrentUser();

        // Validate if the user is part of the project
        if (validateProjectMembership(projectId, currentUser.getId())) {
            throw new UnauthorizedAccessException("You are not part of this project");
        }

        // Fetch the current user's project membership
        ProjectMember currentMember = projectMemberRepository
                .findByUserIdAndProjectId(currentUser.getId(), projectId)
                .orElseThrow(() -> new UnauthorizedAccessException("You are not a member of this project"));

        // Check if the user has the required role to update the project
        if (!currentMember.getRole().getName().equalsIgnoreCase("ADMIN") &&
                !currentMember.getRole().getName().equalsIgnoreCase("MANAGER")) {
            throw new UnauthorizedAccessException("You do not have permission to update this project");
        }

        // Update project fields if provided in the DTO
        if (updateProjectDTO.getName() != null && !updateProjectDTO.getName().isEmpty()) {
            project.setName(updateProjectDTO.getName());
        }
        if (updateProjectDTO.getDescription() != null && !updateProjectDTO.getDescription().isEmpty()) {
            project.setDescription(updateProjectDTO.getDescription());
        }
        if (updateProjectDTO.getStartDate() != null) {
            project.setStartDate(updateProjectDTO.getStartDate());
        }
        if (updateProjectDTO.getEndDate() != null) {
            project.setEndDate(updateProjectDTO.getEndDate());
        }

        // Save the updated project
        projectRepository.save(project);

        // Convert and return the updated project as a DTO
        return DTOConverter.convertToProjectDTO(project, currentUser.getId(), currentMember.getRole().getName());
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


//    @Override
//    public List<ProjectDTO> getAllProject() {
//        User currentUser = authService.getCurrentUser();
//        return projectRepository.findAll().stream().map(project -> convertToProjectDTO(project, currentUser.getId())).toList();
//    }

    public boolean existsById(Long id){
        return projectRepository.existsById(id);
    }

    public boolean validateProjectMembership(Long projectId, Long userId) {
        return !projectMemberRepository.existsByProjectIdAndUserId(projectId, userId);
    }
}
