package com.sagarmalasi.project.controllers;

import com.sagarmalasi.project.domain.dtos.ProjectCreationRequest;
import com.sagarmalasi.project.domain.dtos.ProjectDto;
import com.sagarmalasi.project.services.ProjectService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/projects")
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{projectId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable UUID projectId){
        return ResponseEntity.ok(projectService.getProjectById(projectId));
    }

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectCreationRequest projectCreationRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(projectCreationRequest));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID projectId){
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }
}
