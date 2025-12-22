package com.sagarmalasi.project.repositories;

import com.sagarmalasi.project.domain.entities.User;
import com.sagarmalasi.project.domain.entities.UserPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserPerformanceRepository extends JpaRepository<UserPerformance,UUID> {

    Optional<UserPerformance> findByMember(User member);
}
