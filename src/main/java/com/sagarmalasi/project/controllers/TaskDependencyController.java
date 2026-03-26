package com.sagarmalasi.project.controllers;

import com.sagarmalasi.project.services.TaskDependencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/dependencies")
@RequiredArgsConstructor
public class TaskDependencyController {
    private final TaskDependencyService dependencyService;

    @PostMapping
    public void addDependency(
            @RequestParam UUID predecessorId,
            @RequestParam UUID successorId
    ) {
        dependencyService.addDependency(predecessorId, successorId);
    }
}
