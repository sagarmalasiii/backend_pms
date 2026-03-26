package com.sagarmalasi.project.services;

import com.sagarmalasi.project.domain.entities.Task;
import com.sagarmalasi.project.domain.entities.TaskDependency;
import com.sagarmalasi.project.repositories.TaskDependencyRepository;
import com.sagarmalasi.project.repositories.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskDependencyService {

    private final TaskRepository taskRepository;
    private final TaskDependencyRepository dependencyRepository;

    @Transactional
    @CacheEvict(value = "criticalPathCache", allEntries = true)
    public void addDependency(UUID predecessorId, UUID successorId) {

        Task predecessor = taskRepository.findById(predecessorId)
                .orElseThrow(() -> new IllegalArgumentException("Predecessor task not found"));

        Task successor = taskRepository.findById(successorId)
                .orElseThrow(() -> new IllegalArgumentException("Successor task not found"));

        // 1️⃣ Self dependency
        if (predecessor.getId().equals(successor.getId())) {
            throw new IllegalArgumentException("Task cannot depend on itself");
        }

        // 2️⃣ Must belong to same project
        if (!predecessor.getProject().getId()
                .equals(successor.getProject().getId())) {
            throw new IllegalArgumentException("Tasks must belong to same project");
        }

        // 3️⃣ Duplicate edge
        if (dependencyRepository
                .existsByPredecessorTaskAndSuccessorTask(predecessor, successor)) {
            throw new IllegalStateException("Dependency already exists");
        }

        // 4️⃣ Full cycle detection
        if (createsCycle(predecessor, successor)) {
            throw new IllegalStateException("Dependency creates circular graph");
        }

        TaskDependency dependency = new TaskDependency();
        dependency.setPredecessorTask(predecessor);
        dependency.setSuccessorTask(successor);

        dependencyRepository.save(dependency);
    }

    /**
     * Detects cycle using DFS
     * If we can reach predecessor starting from successor,
     * then adding predecessor → successor creates cycle.
     */
    private boolean createsCycle(Task predecessor, Task successor) {

        Set<UUID> visited = new HashSet<>();
        return dfs(successor, predecessor.getId(), visited);
    }

    private boolean dfs(Task current, UUID targetId, Set<UUID> visited) {

        if (current.getId().equals(targetId)) {
            return true;
        }

        if (visited.contains(current.getId())) {
            return false;
        }

        visited.add(current.getId());

        return current.getSuccessorDependencies().stream()
                .map(TaskDependency::getSuccessorTask)
                .anyMatch(next -> dfs(next, targetId, visited));
    }
}