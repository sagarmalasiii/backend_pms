package com.sagarmalasi.project.services;

import com.sagarmalasi.project.domain.dtos.TaskCompletionRequest;
import com.sagarmalasi.project.domain.dtos.TaskCreationRequest;
import com.sagarmalasi.project.domain.dtos.TaskDto;
import com.sagarmalasi.project.domain.entities.*;
import com.sagarmalasi.project.mappers.TaskMapper;
import com.sagarmalasi.project.repositories.*;
import com.sagarmalasi.project.security.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskMapper taskMapper;
    private final TaskAssignmentRepository taskAssignmentRepository;


    @Transactional
    @PreAuthorize("isAuthenticated()")
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


//Only Manager Can Create tasks
    @PreAuthorize("hasRole('MANAGER')")
    @CacheEvict(value = "criticalPathCache", allEntries = true)
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

        if (request.getPlannedEndDate().isBefore(request.getPlannedStartDate())) {
            throw new IllegalArgumentException("Task end date cannot be before start date");
        }

        if (request.getPlannedStartDate().isBefore(project.getPlannedStartDate())
                || request.getPlannedEndDate().isAfter(project.getPlannedEndDate())) {
            throw new IllegalArgumentException("Task dates must be within project schedule");
        }

        if (project.getStatus() == ProjectStatus.ON_HOLD ||
                project.getStatus() == ProjectStatus.CANCELLED ||
                project.getStatus() == ProjectStatus.COMPLETED) {

            throw new IllegalStateException("Cannot create tasks for project in status: " + project.getStatus());
        }


        Task newTask = taskMapper.toEntity(request);
        newTask.setStatus(TaskStatus.TODO);
        newTask.setProject(project);

        //Initialzation to prevent Null Pointer Exception
        newTask.setTaskAssignments(new ArrayList<>());



        Task task = taskRepository.save(newTask);
        if (project.getStatus() == ProjectStatus.PLANNED) {
            project.setStatus(ProjectStatus.ACTIVE);
        }

        return taskMapper.toDto(task);
    }



    //Only Manager that created the tasks can delete it
    @PreAuthorize("hasRole('MANAGER')")
    @CacheEvict(value = "criticalPathCache", allEntries = true)
    @Transactional
    public void deleteTask(UUID taskId) {
        UUID getCurrentUserId = SecurityUtils.getCurrentUserId();
        Task task = taskRepository.findById(taskId).orElseThrow();
        if(!task.getProject().getManager().getId().equals(getCurrentUserId)){
            throw new AccessDeniedException("You are not the project manager");
        }

        taskRepository.delete(task);
    }

    //Only Manager or assigned Member Can marks as done
    @Transactional
    @CacheEvict(value = "criticalPathCache", allEntries = true)
    public TaskDto markTaskAsDone(UUID taskId, TaskCompletionRequest request) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        UUID currentUserId = SecurityUtils.getCurrentUserId();

        boolean isManager = task.getProject().getManager().getId().equals(currentUserId);

        boolean isAssigned = task.getTaskAssignments().stream()
                .anyMatch(a -> a.getIsActive()
                        && a.getMember().getId().equals(currentUserId));

        if (!isManager && !isAssigned) {
            throw new AccessDeniedException("Cannot complete task");
        }

        // Ensure all predecessor tasks are DONE
        boolean hasIncompletePredecessor =
                task.getPredecessorDependencies().stream()
                        .anyMatch(dep ->
                                dep.getPredecessorTask().getStatus() != TaskStatus.DONE
                        );

        if (hasIncompletePredecessor) {
            throw new IllegalStateException(
                    "Cannot complete task. Predecessor tasks are not completed."
            );
        }

        task.setStatus(TaskStatus.DONE);
        task.setActualEndDate(LocalDate.now());

        Project project = task.getProject();

        boolean allDone = project.getTasks().stream()
                .allMatch(t -> t.getStatus() == TaskStatus.DONE);

        if (allDone) {
            project.setStatus(ProjectStatus.COMPLETED);
            project.setActualEndDate(LocalDate.now());
        }

        return taskMapper.toDto(taskRepository.save(task));
    }






}
