package com.sagarmalasi.project.services;

import com.sagarmalasi.project.domain.entities.*;
import com.sagarmalasi.project.repositories.TaskRepository;
import com.sagarmalasi.project.repositories.UserPerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPerformanceService {

    private final UserPerformanceRepository userPerformanceRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public void updatePerformance(User member) {

        // Get all completed tasks for this member
        List<Task> completedTasks = taskRepository.findByTaskAssignments_MemberAndStatus(member, TaskStatus.DONE);

        int totalCompleted = completedTasks.size();

        double avgDelayRatio = 0;

        if (totalCompleted > 0) {
            double sumDelay = completedTasks.stream()
                    .mapToDouble(t -> {
                        double actual = t.getActualHours() != null ? t.getActualHours() : t.getEstimatedHours();
                        double estimated = t.getEstimatedHours();
                        return (actual - estimated) / estimated; // delay ratio per task
                    })
                    .sum();

            avgDelayRatio = sumDelay / totalCompleted;
        }

        UserPerformance performance = userPerformanceRepository.findByMember(member)
                .orElse(UserPerformance.builder()
                        .member(member)
                        .build());

        performance.setTotalTaskCompleted(totalCompleted);
        performance.setAvgDelayRatio(avgDelayRatio);
        performance.setLastCalculatedAt(LocalDateTime.now());

        userPerformanceRepository.save(performance);
    }

    public UserPerformance getByUser(User member) {
        return userPerformanceRepository.findByMember(member)
                .orElseThrow(() -> new IllegalArgumentException("UserPerformance not found for user"));
    }


}
