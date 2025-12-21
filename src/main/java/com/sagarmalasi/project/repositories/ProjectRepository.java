package com.sagarmalasi.project.repositories;

import com.sagarmalasi.project.domain.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :id")
    int findAssociatedTasksCount(@Param("id") UUID id);

    @Query("SELECT DISTINCT p FROM Project p WHERE p.manager.id = :managerId")
    List<Project> findByManagerId(UUID managerId);

    @Query("""
    SELECT DISTINCT p
    FROM Project p
    JOIN p.tasks t
    JOIN t.taskAssignments ta
    WHERE ta.member.id = :memberId
""")
    List<Project> findByAssignedMember(UUID memberId);


}
