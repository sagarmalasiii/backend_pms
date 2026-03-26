package com.sagarmalasi.project.repositories;

import com.sagarmalasi.project.domain.entities.Task;

import com.sagarmalasi.project.domain.entities.TaskStatus;
import com.sagarmalasi.project.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId")
    List<Task> findAllByProjectId(@Param("projectId") UUID projectId);

    boolean existsByTitleAndProject_Id(String title, UUID projectId);

    List<Task> findAllByProject_IdAndStatus(UUID projectId, TaskStatus status);


    List<Task> findByTaskAssignments_MemberAndStatus(User member, TaskStatus taskStatus);

    List<Task> findByProject_Id(UUID projectId);
}
