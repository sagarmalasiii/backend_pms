package com.sagarmalasi.project.repositories;

import com.sagarmalasi.project.domain.entities.TaskHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository

public interface TaskHistoryRepository extends JpaRepository<TaskHistory, UUID> {
}
