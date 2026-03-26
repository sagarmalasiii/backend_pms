package com.sagarmalasi.project.services;

import com.sagarmalasi.project.domain.dtos.CriticalPathResult;
import com.sagarmalasi.project.domain.dtos.DelaySimulationResult;
import com.sagarmalasi.project.domain.dtos.ResourceOverloadResult;
import com.sagarmalasi.project.domain.dtos.TaskSchedule;
import com.sagarmalasi.project.domain.entities.Task;
import com.sagarmalasi.project.domain.entities.User;
import com.sagarmalasi.project.repositories.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CriticalPathService {

    private final TaskRepository taskRepository;

    @Cacheable(value = "criticalPathCache",key = "#projectId")
    @Transactional
    public CriticalPathResult calculateCriticalPath(UUID projectId) {

        List<Task> tasks = taskRepository.findAllByProjectId(projectId);

        if (tasks.isEmpty()) {
            throw new IllegalStateException("Project has no tasks");
        }

        // Topological order
        List<Task> ordered = topologicalSort(tasks);

        Map<UUID, Long> duration = new HashMap<>();
        Map<UUID, Long> ES = new HashMap<>();
        Map<UUID, Long> EF = new HashMap<>();
        Map<UUID, Long> LS = new HashMap<>();
        Map<UUID, Long> LF = new HashMap<>();

        // Duration calculation
        for (Task task : ordered) {
            long d = task.getPlannedDurationDays();
            duration.put(task.getId(), d);
        }

        // ---- Forward Pass ----
        for (Task task : ordered) {

            long earliestStart = task.getPredecessorDependencies().stream()
                    .map(dep -> EF.get(dep.getPredecessorTask().getId()))
                    .filter(Objects::nonNull)
                    .max(Long::compare)
                    .orElse(0L);

            ES.put(task.getId(), earliestStart);
            EF.put(task.getId(), earliestStart + duration.get(task.getId()));
        }

        long projectDuration = EF.values().stream()
                .max(Long::compare)
                .orElse(0L);

        // ---- Backward Pass ----
        ListIterator<Task> reverseIterator =
                ordered.listIterator(ordered.size());

        while (reverseIterator.hasPrevious()) {

            Task task = reverseIterator.previous();

            long latestFinish;

            if (task.getSuccessorDependencies().isEmpty()) {
                latestFinish = projectDuration;
            } else {
                latestFinish = task.getSuccessorDependencies().stream()
                        .map(dep -> LS.get(dep.getSuccessorTask().getId()))
                        .filter(Objects::nonNull)
                        .min(Long::compare)
                        .orElse(projectDuration);
            }

            LF.put(task.getId(), latestFinish);
            LS.put(task.getId(), latestFinish - duration.get(task.getId()));
        }

        // ---- Build Result ----
        List<TaskSchedule> schedules = new ArrayList<>();
        List<UUID> criticalTasks = new ArrayList<>();

        for (Task task : ordered) {

            long slack = LS.get(task.getId()) - ES.get(task.getId());
            boolean critical = slack == 0;

            if (critical) {
                criticalTasks.add(task.getId());
            }

            schedules.add(TaskSchedule.builder()
                    .taskId(task.getId())
                    .duration(duration.get(task.getId()))
                    .earlyStart(ES.get(task.getId()))
                    .earlyFinish(EF.get(task.getId()))
                    .lateStart(LS.get(task.getId()))
                    .lateFinish(LF.get(task.getId()))
                    .slack(slack)
                    .critical(critical)
                    .build());
        }

        return CriticalPathResult.builder()
                .taskSchedules(schedules)
                .criticalPathTaskIds(criticalTasks)
                .projectDurationDays(projectDuration)
                .build();
    }

    // ---- Kahn’s Algorithm ----
    private List<Task> topologicalSort(List<Task> tasks) {

        Map<UUID, Integer> inDegree = new HashMap<>();
        Map<UUID, Task> taskMap = new HashMap<>();

        for (Task task : tasks) {
            inDegree.put(task.getId(), task.getPredecessorDependencies().size());
            taskMap.put(task.getId(), task);
        }

        Queue<Task> queue = new LinkedList<>();

        for (Task task : tasks) {
            if (inDegree.get(task.getId()) == 0) {
                queue.add(task);
            }
        }

        List<Task> ordered = new ArrayList<>();

        while (!queue.isEmpty()) {
            Task current = queue.poll();
            ordered.add(current);

            current.getSuccessorDependencies().forEach(dep -> {
                UUID successorId = dep.getSuccessorTask().getId();
                inDegree.put(successorId, inDegree.get(successorId) - 1);

                if (inDegree.get(successorId) == 0) {
                    queue.add(taskMap.get(successorId));
                }
            });
        }

        if (ordered.size() != tasks.size()) {
            throw new IllegalStateException("Cycle detected in task graph");
        }

        return ordered;
    }

    public DelaySimulationResult simulateDelay(UUID projectId,
                                               UUID taskId,
                                               int delayDays) {

        CriticalPathResult result = calculateCriticalPath(projectId);

        TaskSchedule schedule = result.getTaskSchedules()
                .stream()
                .filter(t -> t.getTaskId().equals(taskId))
                .findFirst()
                .orElseThrow();

        int slack = Math.toIntExact(schedule.getSlack());
        boolean critical = schedule.isCritical();

        int projectDelay = 0;

        if (delayDays > slack) {
            projectDelay = delayDays - slack;
        }

        int newDuration = Math.toIntExact(result.getProjectDurationDays() + projectDelay);

        return new DelaySimulationResult(
                taskId,
                delayDays,
                slack,
                critical,
                projectDelay,
                newDuration
        );
    }


    public List<ResourceOverloadResult> detectOverload(UUID projectId) {

        List<Task> tasks = taskRepository.findByProject_Id(projectId);

        Map<User, List<Task>> grouped =
                tasks.stream()
                        .flatMap(t -> t.getTaskAssignments().stream()
                                .map(a -> Map.entry(a.getMember(), t)))
                        .collect(Collectors.groupingBy(
                                Map.Entry::getKey,
                                Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                        ));

        List<ResourceOverloadResult> results = new ArrayList<>();

        for (var entry : grouped.entrySet()) {

            User user = entry.getKey();
            List<Task> userTasks = entry.getValue();

            userTasks.sort(Comparator.comparing(Task::getPlannedStartDate));

            boolean overload = false;

            for (int i = 1; i < userTasks.size(); i++) {
                Task prev = userTasks.get(i - 1);
                Task current = userTasks.get(i);

                if (current.getPlannedStartDate()
                        .isBefore(prev.getPlannedEndDate())) {
                    overload = true;
                    break;
                }
            }

            results.add(new ResourceOverloadResult(
                    user.getId(),
                    user.getUsername(),
                    overload,
                    userTasks.size()
            ));
        }

        return results;
    }

}