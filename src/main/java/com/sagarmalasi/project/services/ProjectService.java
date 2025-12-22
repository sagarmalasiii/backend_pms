package com.sagarmalasi.project.services;

import com.sagarmalasi.project.domain.Role;
import com.sagarmalasi.project.domain.dtos.ProjectCreationRequest;
import com.sagarmalasi.project.domain.dtos.ProjectDto;
import com.sagarmalasi.project.domain.entities.Project;
import com.sagarmalasi.project.domain.entities.User;
import com.sagarmalasi.project.mappers.ProjectMapper;
import com.sagarmalasi.project.repositories.ProjectRepository;
import com.sagarmalasi.project.repositories.UserRepository;
import com.sagarmalasi.project.security.SecurityUtils;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserRepository userRepository;

    @PreAuthorize("isAuthenticated()")
    public List<ProjectDto> getAllProjects() {

        UUID userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Project> projects;

        switch (user.getRole()) {

            case ADMIN -> projects = projectRepository.findAll();

            case MANAGER -> projects = projectRepository.findByManagerId(userId);

            case MEMBER -> projects = projectRepository.findByAssignedMember(userId);

            default -> throw new IllegalStateException("Unsupported role");
        }

        return projects.stream()
                .map(projectMapper::toDto)
                .toList();
    }


    @PreAuthorize("hasRole('MANAGER')")
    @Transactional
    public ProjectDto createProject(@Valid ProjectCreationRequest request) {
        UUID managerId = SecurityUtils.getCurrentUserId();

        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Project project = projectMapper.toEntity(request);
        project.setManager(manager);

        return projectMapper.toDto(projectRepository.save(project));
    }

    @PreAuthorize("isAuthenticated()")
    public ProjectDto getProjectById(UUID projectId) {

        UUID userId = SecurityUtils.getCurrentUserId();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean allowed = switch (user.getRole()) {

            case ADMIN -> true;

            case MANAGER -> project.getManager().getId().equals(userId);

            case MEMBER -> project.getTasks().stream()
                    .flatMap(task -> task.getTaskAssignments().stream())
                    .anyMatch(ta -> ta.getMember().getId().equals(userId));

            default -> false;
        };

        if (!allowed) {
            throw new AccessDeniedException("You are not allowed to view this project");
        }

        return projectMapper.toDto(project);
    }


    @PreAuthorize("hasRole('MANAGER')")
    @Transactional
    public void deleteProject(UUID id) {

        UUID currentUserId = SecurityUtils.getCurrentUserId();

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        if (!project.getManager().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You are not the project manager");
        }

        if (projectRepository.findAssociatedTasksCount(id) > 0) {
            throw new IllegalStateException("Project has associated tasks; it cannot be deleted");
        }

        projectRepository.delete(project);
    }

}
