package com.sagarmalasi.project.controllers;

import com.sagarmalasi.project.domain.dtos.TaskDto;
import com.sagarmalasi.project.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping("/{projectId}")
    public ResponseEntity<List<TaskDto>> getAllProjectAssociatedTasks(@PathVariable UUID projectId){
        return ResponseEntity.ok(taskService.getAllProjectAssociatedTasks(projectId));

    }

}
