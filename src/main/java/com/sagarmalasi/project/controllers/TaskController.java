package com.sagarmalasi.project.controllers;

import com.sagarmalasi.project.domain.dtos.TaskCompletionRequest;
import com.sagarmalasi.project.domain.dtos.TaskCreationRequest;
import com.sagarmalasi.project.domain.dtos.TaskDto;
import com.sagarmalasi.project.services.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping(path = "/create-task")
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskCreationRequest taskCreationRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(taskCreationRequest));
    }

    @DeleteMapping(path = "/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID taskId){
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();

    }



    @PostMapping("/{taskId}/done")
    public ResponseEntity<TaskDto> markTaskAsDone(
            @PathVariable UUID taskId,
            @RequestBody @Valid TaskCompletionRequest request) {

        return ResponseEntity.ok(
                taskService.markTaskAsDone(taskId, request)
        );
    }







}
