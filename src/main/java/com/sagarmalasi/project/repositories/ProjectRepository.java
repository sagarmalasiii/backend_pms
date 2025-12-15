package com.sagarmalasi.project.repositories;

import com.sagarmalasi.project.domain.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :id")
    int findAssociatedTasksCount(@Param("id") UUID id);

}
