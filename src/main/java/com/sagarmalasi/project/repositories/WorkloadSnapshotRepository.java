package com.sagarmalasi.project.repositories;

import com.sagarmalasi.project.domain.entities.WorkloadSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorkloadSnapshotRepository extends JpaRepository<WorkloadSnapshot, UUID> {
}
