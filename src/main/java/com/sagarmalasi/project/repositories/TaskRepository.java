package com.sagarmalasi.project.repositories;

import com.sagarmalasi.project.domain.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    @Query("SELECT t FROM Task t JOIN FETCH t.project p WHERE p.id = :projectId")
    List<Task> findAllTasks(@Param("projectId") UUID projectId);
}
