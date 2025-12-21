package com.sagarmalasi.project.services;

import com.sagarmalasi.project.domain.dtos.TaskCreationRequest;
import com.sagarmalasi.project.domain.dtos.TaskDto;
import com.sagarmalasi.project.domain.entities.Project;
import com.sagarmalasi.project.domain.entities.Task;
import com.sagarmalasi.project.domain.entities.TaskStatus;
import com.sagarmalasi.project.mappers.TaskMapper;
import com.sagarmalasi.project.repositories.ProjectRepository;
import com.sagarmalasi.project.repositories.TaskAssignmentRepository;
import com.sagarmalasi.project.repositories.TaskRepository;
import com.sagarmalasi.project.security.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskMapper taskMapper;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final UserPerformanceService userPerformanceService;
    private final RiskAssessmentService riskAssessmentService;

    public List<TaskDto> getAllProjectAssociatedTasks(UUID projectId) {
        UUID userId = SecurityUtils.getCurrentUserId();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        boolean allowed =
                project.getManager().getId().equals(userId)
                        || taskAssignmentRepository
                        .existsByTask_Project_IdAndMember_Id(projectId, userId);

        if (!allowed) {
            throw new AccessDeniedException("You cannot view tasks of this project");
        }

        return taskRepository.findAllByProjectId(projectId)
                .stream()
                .map(taskMapper::toDto)
                .toList();
    }



    @Transactional
    public TaskDto createTask(TaskCreationRequest request) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        if (!project.getManager().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Only project manager can create tasks");
        }

        if (taskRepository.existsByTitleAndProject_Id(
                request.getTitle(), request.getProjectId())) {
            throw new IllegalArgumentException("Task title already exists in this project");
        }

        Task newTask = taskMapper.toEntity(request);
        newTask.setProject(project);
        Task task = taskRepository.save(newTask);
        riskAssessmentService.assessTaskRisk(task);

        return taskMapper.toDto(task);
    }



    @Transactional
    public void deleteTask(UUID taskId) {
        UUID getCurrentUserId = SecurityUtils.getCurrentUserId();
        Task task = taskRepository.findById(taskId).orElseThrow();
        if(!task.getProject().getManager().getId().equals(getCurrentUserId)){
            throw new AccessDeniedException("You are not the project manager");
        }

        taskRepository.delete(task);
    }


    @Transactional
    public TaskDto markTaskCompleted(UUID taskId) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        Task task = taskRepository.findById(taskId).orElseThrow();

        // Check if current user is assigned or manager
        boolean allowed = task.getProject().getManager().getId().equals(currentUserId) ||
                task.getTaskAssignments().stream()
                        .anyMatch(a -> a.getMember().getId().equals(currentUserId));

        if (!allowed) throw new AccessDeniedException("You cannot complete this task");

        task.setStatus(TaskStatus.DONE);
        task.setCompletedAt(LocalDateTime.now());

        taskRepository.save(task);

        // Update performance for all assigned members
        task.getTaskAssignments().forEach(a -> {
            userPerformanceService.updatePerformance(a.getMember());
        });

        return taskMapper.toDto(task);
    }

}
