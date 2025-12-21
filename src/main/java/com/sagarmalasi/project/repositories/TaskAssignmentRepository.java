package com.sagarmalasi.project.repositories;

import com.sagarmalasi.project.domain.entities.TaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, UUID> {
    boolean existsByTask_Project_IdAndMember_Id(UUID projectId, UUID memberId);
    boolean existsByTask_IdAndMember_IdAndIsActiveTrue(UUID taskId, UUID memberId);

    List<TaskAssignment> findByTask_IdAndIsActiveTrue(UUID taskId);

    Optional<TaskAssignment> findByTask_IdAndMember_IdAndIsActiveTrue(
            UUID taskId, UUID memberId
    );
}