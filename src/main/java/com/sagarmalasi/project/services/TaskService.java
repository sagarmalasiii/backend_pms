package com.sagarmalasi.project.services;

import com.sagarmalasi.project.domain.dtos.TaskCompletionRequest;
import com.sagarmalasi.project.domain.dtos.TaskCreationRequest;
import com.sagarmalasi.project.domain.dtos.TaskDto;
import com.sagarmalasi.project.domain.entities.*;
import com.sagarmalasi.project.mappers.TaskMapper;
import com.sagarmalasi.project.repositories.ProjectRepository;
import com.sagarmalasi.project.repositories.TaskAssignmentRepository;
import com.sagarmalasi.project.repositories.TaskRepository;
import com.sagarmalasi.project.security.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    private final WorkloadSnapShotService workloadSnapShotService;

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
    public TaskDto markTaskAsDone(UUID taskId, TaskCompletionRequest request) {

        UUID currentUserId = SecurityUtils.getCurrentUserId();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (task.getStatus() == TaskStatus.DONE) {
            throw new IllegalStateException("Task already marked as DONE");
        }

        boolean isManager =
                task.getProject().getManager().getId().equals(currentUserId);

        boolean isAssignedMember =
                task.getTaskAssignments().stream()
                        .anyMatch(a ->
                                a.getIsActive() &&
                                        a.getMember().getId().equals(currentUserId)
                        );

        if (!isManager && !isAssignedMember) {
            throw new AccessDeniedException("You cannot complete this task");
        }

        // 1️⃣ Update task
        task.setStatus(TaskStatus.DONE);
        task.setActualHours(request.getActualHours());
        task.setCompletedAt(LocalDateTime.now());

        // 2️⃣ Task history
        TaskHistory history = new TaskHistory();
        history.setTask(task);
        history.setPreviousStatus(TaskStatus.IN_PROGRESS);
        history.setNewStatus(TaskStatus.DONE);
        history.setChangedAt(LocalDateTime.now());
        task.getTaskHistories().add(history);

        // 3️⃣ Update performance
        updateUserPerformance(task);

        // 4️⃣ Workload snapshot
        updateWorkloadSnapshot(task);

        // 5️⃣ Risk assessment
        assessFinalRisk(task);

        return taskMapper.toDto(taskRepository.save(task));
    }


    private void updateUserPerformance(Task task) {

        task.getTaskAssignments().stream()
                .filter(TaskAssignment::getIsActive)
                .forEach(assignment -> {

                    User member = assignment.getMember();
                    UserPerformance performance = member.getPerformance();

                    if (performance == null) {
                        performance = new UserPerformance();
                        performance.setMember(member);
                        member.setPerformance(performance);
                    }

                    performance.setTotalTaskCompleted(
                            performance.getTotalTaskCompleted() + 1
                    );

                    double delayRatio =
                            (double) (task.getActualHours() - task.getEstimatedHours())
                                    / task.getEstimatedHours();

                    performance.setAvgDelayRatio(
                            performance.getAvgDelayRatio() == null
                                    ? delayRatio
                                    : (performance.getAvgDelayRatio() + delayRatio) / 2
                    );
                });
    }
    private void updateWorkloadSnapshot(Task task) {

        task.getTaskAssignments().stream()
                .filter(TaskAssignment::getIsActive)
                .forEach(assignment -> {

                    User member = assignment.getMember();

                    long activeTaskCount =
                            member.getAssignments().stream()
                                    .filter(TaskAssignment::getIsActive)
                                    .count();

                    WorkloadSnapshot snapshot = new WorkloadSnapshot();
                    snapshot.setMember(member);
                    snapshot.setActiveTaskCount((int) activeTaskCount);
                    snapshot.setTask(task);

                    member.getWorkloadSnapshots().add(snapshot);
                });
    }

    private void assessFinalRisk(Task task) {

        double ratio = (double) task.getActualHours()/task.getEstimatedHours();

        RiskLevel risk =
                ratio <= 1.1 ? RiskLevel.LOW :
                        ratio <= 1.3 ? RiskLevel.MEDIUM :
                                RiskLevel.HIGH;

        RiskAssessment assessment = RiskAssessment.builder()
                .task(task)
                .estimatedHours(task.getEstimatedHours())
                .predictedCompletionHours(task.getActualHours())
                .riskLevel(risk)
                .modelVersion("rule-v1")
                .assessedAt(LocalDateTime.now())
                .build();

        task.getRiskAssessments().add(assessment);
    }





}
