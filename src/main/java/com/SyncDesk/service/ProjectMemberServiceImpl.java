package com.SyncDesk.service;

import com.SyncDesk.dto.project_member.AddMemberDTO;
import com.SyncDesk.dto.project_member.ChangeRoleDTO;
import com.SyncDesk.dto.project_member.ProjectMemberDTO;
import com.SyncDesk.entity.Project;
import com.SyncDesk.entity.ProjectMember;
import com.SyncDesk.entity.Role;
import com.SyncDesk.entity.User;
import com.SyncDesk.repository.ProjectMemberRepository;
import com.SyncDesk.repository.ProjectRepository;
import com.SyncDesk.repository.RoleRepository;
import com.SyncDesk.repository.UserRepository;
import com.SyncDesk.utils.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.SyncDesk.utils.DTOConverter.convertToProjectMemberDTO;

@Service
public class ProjectMemberServiceImpl implements ProjectMemberService{

    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final RoleRepository roleRepository;

    public ProjectMemberServiceImpl(ProjectMemberRepository projectMemberRepository, UserRepository userRepository, ProjectRepository projectRepository, RoleRepository roleRepository) {
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public List<ProjectMemberDTO> getAllByProjectId(Long id) throws NoProjectFoundException {
        List<ProjectMember> projectMembers = projectMemberRepository.findAllByProjectId(id);

        if (projectMembers.isEmpty()) {
            throw new NoProjectFoundException("No members found for the project with ID: " + id);
        }

        return projectMembers.stream()
                .map(DTOConverter::convertToProjectMemberDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProjectMemberDTO addMember(Long id, AddMemberDTO member) {
        validateRole(member.getRole());

        String currentUserEmail = getCurrentUserEmail();

        User currentUser = getCurrentUser(currentUserEmail);
        ProjectMember currentMember = getCurrentMember(currentUser.getId());

        Project project = getProjectById(id);

        Role memberRole = getMemberRole(member.getRole());


        if (!isAuthorized(currentMember.getRole().getName())) {
            throw new RuntimeException("Only admins or project managers can add new members.");
        }

        User memberToAdd = userRepository.findByEmail(member.getEmail())
                .orElseThrow(() -> new NoSuchUserFoundException("User not found. Please ask them to register on the platform."));

        if (projectMemberRepository.existsByProjectAndUser(project, memberToAdd)) {
            throw new MemberAlreadyExistsException("User is already a member of this project.");
        }

        ProjectMember projectMember = new ProjectMember();
        projectMember.setUser(memberToAdd);
        projectMember.setProject(project);
        projectMember.setRole(memberRole);
        projectMember.setJoinedAt(LocalDate.now());
        projectMemberRepository.save(projectMember);

        return convertToProjectMemberDTO(projectMember);
    }

    public ProjectMemberDTO editMember(Long id, String newRole) {

        String currentUserEmail = getCurrentUserEmail();

        User currentUser = getCurrentUser(currentUserEmail);
        ProjectMember currentMember = getCurrentMember(currentUser.getId());

        Role memberRole = getMemberRole(newRole);

        if (!isAuthorized(currentMember.getRole().getName(), memberRole.getName())) {
            throw new RuntimeException("Only admins or project managers can change members role.");
        }

        ProjectMember memberToChange = projectMemberRepository.findById(id)
                .orElseThrow(() ->  new RuntimeException("No member found"));

        memberToChange.setRole(memberRole);
        projectMemberRepository.save(memberToChange);

        return convertToProjectMemberDTO(memberToChange);
    }

    private boolean isAuthorized(String currentRole, String targetRole) {
        if ("Manager".equalsIgnoreCase(currentRole) && "Admin".equalsIgnoreCase(targetRole)) {
            throw new UnauthorizedActionException("Managers cannot assign the Admin role");
        }
        return "Admin".equalsIgnoreCase(currentRole) || "Manager".equalsIgnoreCase(currentRole);
    }


    private ProjectMember getCurrentMember(Long id) {
        return projectMemberRepository.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("You are not a member of this project"));
    }

    private Role getMemberRole(String role) {
        return roleRepository.findByName(role)
                .orElseThrow(() -> new RuntimeException("Invalid role specified"));
    }

    private Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No project found with the provided ID"));
    }

    private User getCurrentUser(String currentUserEmail) {
        return userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new NoSuchUserFoundException("Unauthorized access"));
    }

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private void validateRole(String roleName) {
        if ("Admin".equalsIgnoreCase(roleName)) {
            throw new RuntimeException("Admin role cannot be assigned");
        }
    }

    private boolean isAuthorized(String role) {
        return "Admin".equals(role) || "Manager".equals(role);
    }

}
