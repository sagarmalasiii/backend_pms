package com.sagarmalasi.project.repositories;

import com.sagarmalasi.project.domain.entities.Task;
import com.sagarmalasi.project.domain.entities.TaskDependency;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TaskDependencyRepository extends JpaRepository<TaskDependency, UUID> {
    boolean existsByPredecessorTaskAndSuccessorTask(Task predecessor, Task successor);
}
