package com.sagarmalasi.project.controllers;

import com.sagarmalasi.project.domain.dtos.RiskAssessmentDto;
import com.sagarmalasi.project.domain.entities.RiskAssessment;
import com.sagarmalasi.project.domain.entities.Task;
import com.sagarmalasi.project.mappers.RiskAssessmentMapper;
import com.sagarmalasi.project.repositories.TaskRepository;
import com.sagarmalasi.project.services.RiskAssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/risk")
@RequiredArgsConstructor
public class RiskAssessmentController {
    private final RiskAssessmentService riskAssessmentService;
    private final TaskRepository taskRepository;
    private final RiskAssessmentMapper riskAssessmentMapper;

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<List<RiskAssessmentDto>> getRiskForTask(@PathVariable UUID taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        List<RiskAssessment> assessments = riskAssessmentService.getAllForTask(task);
        return ResponseEntity.ok(
                assessments.stream()
                        .map(riskAssessmentMapper::toDto)
                        .toList()
        );
    }
}
