package com.sagarmalasi.project.controllers;

import com.sagarmalasi.project.domain.dtos.CriticalPathResult;
import com.sagarmalasi.project.domain.dtos.DelaySimulationRequest;
import com.sagarmalasi.project.domain.dtos.DelaySimulationResult;
import com.sagarmalasi.project.domain.dtos.ResourceOverloadResult;
import com.sagarmalasi.project.services.CriticalPathService;
import com.sagarmalasi.project.services.TaskDependencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/projects")
@RequiredArgsConstructor
public class CriticalPathController {

    private final CriticalPathService criticalPathService;

    @GetMapping("/{projectId}/critical-path")
    public CriticalPathResult getCriticalPath(@PathVariable UUID projectId) {
        return criticalPathService.calculateCriticalPath(projectId);
    }

    @PostMapping("/{projectId}/simulate-delay")
    public DelaySimulationResult simulateDelay(
            @PathVariable UUID projectId,
            @RequestBody DelaySimulationRequest request) {

        return criticalPathService.simulateDelay(
                projectId,
                request.taskId(),
                request.delayDays()
        );
    }

    @GetMapping("/{projectId}/resource-overload")
    public List<ResourceOverloadResult> checkOverload(
            @PathVariable UUID projectId) {

        return criticalPathService.detectOverload(projectId);
    }
}
