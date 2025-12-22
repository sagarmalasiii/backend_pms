package com.sagarmalasi.project.repositories;

import com.sagarmalasi.project.domain.entities.WorkloadSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorkloadSnapshotRepository extends JpaRepository<WorkloadSnapshot, UUID> {

        @Query("""
        SELECT COUNT(ta)
        FROM TaskAssignment ta
        WHERE ta.member.id = :userId
          AND ta.isActive = true
    """)
        long countActiveTasksForUser(@Param("userId") UUID userId);
    }


