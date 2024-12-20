package com.SyncDesk.service;

import com.SyncDesk.dto.task.CreateTaskDTO;
import com.SyncDesk.dto.task.TaskResponseDTO;
import com.SyncDesk.entity.*;
import com.SyncDesk.repository.ProjectMemberRepository;
import com.SyncDesk.repository.ProjectRepository;
import com.SyncDesk.repository.TaskRepository;
import com.SyncDesk.repository.UserRepository;
import com.SyncDesk.utils.DTOConverter;
import com.SyncDesk.utils.ResourceNotFoundException;
import com.SyncDesk.utils.UnauthorizedAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    public final TaskRepository taskRepository;
    public final ProjectRepository projectRepository;
    public final AuthService authService;
    public final UserRepository userRepository;
    public final ProjectMemberRepository projectMemberRepository;


    public TaskServiceImpl(TaskRepository taskRepository, ProjectRepository projectRepository, AuthService authService, UserRepository userRepository, ProjectMemberRepository projectMemberRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.authService = authService;
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    @Override
    public List<TaskResponseDTO> findAllByProjectId(Long projectId) {
        User currentUser = authService.getCurrentUser();
        if (validateProjectMembership(projectId, currentUser.getId())) {
            throw new UnauthorizedAccessException("You are not a member of this project.");
        }
        List<Task> tasks = taskRepository.findAllByProjectId(projectId);
        return tasks.stream()
                .map(DTOConverter::convertToTaskResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponseDTO getTaskById(Long projectId, Long taskId) throws ResourceNotFoundException {
        User currentUser = authService.getCurrentUser();
        if(validateProjectMembership(projectId, currentUser.getId())){
            throw new UnauthorizedAccessException("Unable to fetch tha data");
        }
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if(!Objects.equals(task.getProject().getId(), projectId)){
            throw new ResourceNotFoundException("Task not found");
        }
        return DTOConverter.convertToTaskResponseDTO(task);
    }

    @Override
    public TaskResponseDTO createTask(Long projectId, CreateTaskDTO createTaskDTO) throws ResourceNotFoundException {
        Project project = projectRepository
                .findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project with ID " + projectId + " not found."));

        User user = authService.getCurrentUser();


        if (validateProjectMembership(projectId, user.getId())) {
            throw new UnauthorizedAccessException("You are not a part of this project.");
        }

        Task task = new Task();
        task.setName(createTaskDTO.getName());
        if (createTaskDTO.getDescription() != null && !createTaskDTO.getDescription().isEmpty()) {
            task.setDescription(createTaskDTO.getDescription());
        }
        task.setProject(project);
        if (createTaskDTO.getAssignedTo() != null) {
            ProjectMember assignedMember = validateAndGetAssignedMember(createTaskDTO.getAssignedTo(), projectId);
            task.setAssignedTo(assignedMember);
        }

        task.setCreatedBy(getMemberByUserId(user.getId(), projectId));

        task.setPriority(createTaskDTO.getPriority() != null
                ? TaskPriority.valueOf(createTaskDTO.getPriority())
                : TaskPriority.LOW);
        task.setStatus(createTaskDTO.getStatus() != null
                ? TaskStatus.valueOf(createTaskDTO.getStatus())
                : TaskStatus.TO_DO);
        task.setDueDate(createTaskDTO.getDueDate());
        taskRepository.save(task);

        return DTOConverter.convertToTaskResponseDTO(task);
    }

    @Override
    public TaskResponseDTO updateTask(Long projectId, Long taskId, CreateTaskDTO updateTaskDTO)
            throws ResourceNotFoundException {
        Project project = projectRepository
                .findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project with ID " + projectId + " not found."));

        User currentUser = authService.getCurrentUser();
        if (validateProjectMembership(projectId, currentUser.getId())) {
            throw new UnauthorizedAccessException("You are not part of this project");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with ID " + taskId + " not found."));
        System.out.println("Task found: " + task.getId());
        if (!Objects.equals(task.getProject().getId(), projectId)) {
            throw new ResourceNotFoundException("Task does not belong to the project");
        }

        if (updateTaskDTO.getName() != null && !updateTaskDTO.getName().isEmpty()) {
            task.setName(updateTaskDTO.getName());
        }
        if (updateTaskDTO.getDescription() != null && !updateTaskDTO.getDescription().isEmpty()) {
            task.setDescription(updateTaskDTO.getDescription());
        }
        if (updateTaskDTO.getPriority() != null && !updateTaskDTO.getPriority().isEmpty()) {
            task.setPriority(TaskPriority.valueOf(updateTaskDTO.getPriority()));
        }
        if (updateTaskDTO.getStatus() != null && !updateTaskDTO.getStatus().isEmpty()) {
            task.setStatus(TaskStatus.valueOf(updateTaskDTO.getStatus()));
        }
        if (updateTaskDTO.getDueDate() != null) {
            task.setDueDate(updateTaskDTO.getDueDate());
        }

        if (updateTaskDTO.getAssignedTo() != null) {
            ProjectMember assignedMember = validateAndGetAssignedMember(updateTaskDTO.getAssignedTo(), projectId);
            task.setAssignedTo(assignedMember);
        }

        taskRepository.save(task);

        return DTOConverter.convertToTaskResponseDTO(task);
    }

    @Override
    public void deleteTask(Long projectId, Long taskId) throws ResourceNotFoundException {
        User currentUser = authService.getCurrentUser();

        if (validateProjectMembership(projectId, currentUser.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to perform this action.");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with ID " + taskId + " not found."));

        if (!Objects.equals(task.getProject().getId(), projectId)) {
            throw new ResourceNotFoundException("Task does not belong to the specified project.");
        }

        ProjectMember currentMember = getMemberByUserId(currentUser.getId(), projectId);

        if (!isAdminOrManager(currentMember)) {
            // Check if the user is a MEMBER and either created or was assigned the task
            boolean isCreatorOrAssignee = isTaskCreatorOrAssignee(currentMember, task);
            if (!isCreatorOrAssignee) {
                throw new UnauthorizedAccessException("You do not have permission to delete this task.");
            }
        }

        taskRepository.delete(task);
    }

    private ProjectMember validateAndGetAssignedMember(Long assignedToId, Long projectId) {
        if (assignedToId == null) {
            return null;
        }
        return getMemberById(assignedToId, projectId);
    }


    private Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No project found with the provided ID"));
    }
    private ProjectMember getMemberByUserId(Long id, Long projectId) {
        return projectMemberRepository.findByUserIdAndProjectId(id, projectId)
                .orElseThrow(() -> new RuntimeException("Unauthorized: You are not part of the project"));
    }
    private ProjectMember getMemberById(Long id, Long projectId) {
        return projectMemberRepository.findByIdAndProjectId(id, projectId)
                .orElseThrow(() -> new RuntimeException("Provided user is not a member of this project (MemberId: " + id + ")"));
    }

    public boolean validateProjectMembership(Long projectId, Long userId) {
        return !projectMemberRepository.existsByProjectIdAndUserId(projectId, userId);
    }

    private boolean isAdminOrManager(ProjectMember member) {
        String roleName = member.getRole().getName().toUpperCase();
        return "ADMIN".equals(roleName) || "MANAGER".equals(roleName);
    }

    private boolean isTaskCreatorOrAssignee(ProjectMember member, Task task) {
        Long memberId = member.getUser().getId();
        return (task.getCreatedBy().getUser().getId().equals(memberId)) ||
                (task.getAssignedTo() != null && task.getAssignedTo().getUser().getId().equals(memberId));
    }

}
