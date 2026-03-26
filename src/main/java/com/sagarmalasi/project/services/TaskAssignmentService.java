package com.sagarmalasi.project.services;

import com.sagarmalasi.project.domain.Role;
import com.sagarmalasi.project.domain.entities.Task;
import com.sagarmalasi.project.domain.entities.TaskAssignment;
import com.sagarmalasi.project.domain.entities.User;
import com.sagarmalasi.project.repositories.TaskAssignmentRepository;
import com.sagarmalasi.project.repositories.TaskRepository;
import com.sagarmalasi.project.repositories.UserRepository;
import com.sagarmalasi.project.security.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskAssignmentService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;


    //Assign user a task
    @Transactional
    public void assignUserToTask(UUID taskId, UUID userId) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        // 1. Authorization
        if (!task.getProject().getManager().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Only project manager can assign tasks");
        }

        // 2. User existence
        User member = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 3. Role check
        if (member.getRole() != Role.MEMBER) {
            throw new IllegalArgumentException("Only members can be assigned tasks");
        }

        // 4. Prevent duplicate active assignment
        if (taskAssignmentRepository
                .existsByTask_IdAndMember_IdAndIsActiveTrue(taskId, userId)) {
            throw new IllegalStateException("User already assigned to this task");
        }

        // 5. Create assignment
        TaskAssignment assignment = TaskAssignment.builder()
                .task(task)
                .member(member)
                .isActive(true)
                .build();

        taskAssignmentRepository.save(assignment);

    }
//Unassign user from task
    @Transactional
    public void unassignUserFromTask(UUID taskId, UUID userId) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (!task.getProject().getManager().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Only project manager can unassign tasks");
        }

        TaskAssignment assignment = taskAssignmentRepository
                .findByTask_IdAndMember_IdAndIsActiveTrue(taskId, userId)
                .orElseThrow(() -> new IllegalStateException("Assignment not found"));

        assignment.setIsActive(false);
        assignment.setUnassignedAt(LocalDateTime.now());

    }

}
