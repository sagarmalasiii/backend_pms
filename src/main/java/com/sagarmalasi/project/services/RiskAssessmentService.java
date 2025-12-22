package com.sagarmalasi.project.services;

import com.sagarmalasi.project.domain.entities.*;
import com.sagarmalasi.project.repositories.RiskAssessmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RiskAssessmentService {

    private final RiskAssessmentRepository riskAssessmentRepository;

    @Transactional
    public RiskAssessment assessTaskRisk(Task task) {

        double predictedHours = predictCompletionHours(task);
        RiskLevel riskLevel = determineRiskLevel(task, predictedHours);

        RiskAssessment assessment = RiskAssessment.builder()
                .task(task)
                .predictedCompletionHours((int) predictedHours)
                .estimatedHours(task.getEstimatedHours())
                .riskLevel(riskLevel)
                .modelVersion("v1-simple")
                .build();

        return riskAssessmentRepository.save(assessment);
    }

    /**
     * Simple prediction logic:
     * Predicted hours = estimated hours adjusted by workload
     */
    private double predictCompletionHours(Task task) {
        int activeTasks = task.getTaskAssignments().size(); // very simple metric
        double estimated = task.getEstimatedHours();
        // increase predicted time by 10% for each active task assigned to the member
        return estimated * (1 + 0.1 * activeTasks);
    }

    private RiskLevel determineRiskLevel(Task task, double predictedHours) {
        double ratio = predictedHours / task.getEstimatedHours();
        if (ratio <= 1.2) return RiskLevel.LOW;
        if (ratio <= 1.5) return RiskLevel.MEDIUM;
        return RiskLevel.HIGH;
    }

    public List<RiskAssessment> getAllForTask(Task task) {
        return riskAssessmentRepository.findByTask_Id(task.getId());
    }
}
