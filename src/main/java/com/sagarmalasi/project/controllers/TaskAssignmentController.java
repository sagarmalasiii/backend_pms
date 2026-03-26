package com.sagarmalasi.project.controllers;

import com.sagarmalasi.project.domain.dtos.TaskAssignmentRequest;
import com.sagarmalasi.project.services.TaskAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/tasks")
public class TaskAssignmentController {

    private final TaskAssignmentService taskAssignmentService;

    //Assigne task to the member
    @PostMapping("/{taskId}/assign")
    public ResponseEntity<Void> assignUserToTask(
            @PathVariable UUID taskId,
            @RequestBody @Valid TaskAssignmentRequest request
    ) {
        taskAssignmentService.assignUserToTask(taskId, request.getUserId());
        return ResponseEntity.ok().build();
    }


    //Unassign memeber from task
    @PostMapping("/{taskId}/unassign")
    public ResponseEntity<Void> unassignUserFromTask(
            @PathVariable UUID taskId,
            @RequestBody @Valid TaskAssignmentRequest request
    ) {
        taskAssignmentService.unassignUserFromTask(taskId, request.getUserId());
        return ResponseEntity.ok().build();
    }
}
