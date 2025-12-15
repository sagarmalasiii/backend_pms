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

    public List<ProjectDto> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream().map(projectMapper::toDto).toList();

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

    public  ProjectDto getProjectById(UUID id) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Project cannot be found"));
        return projectMapper.toDto(project);
    }

    @PreAuthorize("hasRole('MANAGER')")
    public void deleteProject(UUID id) {
        if (projectRepository.findAssociatedTasksCount(id) > 0) {
            throw new IllegalStateException("Project has associated tasks; it cannot be deleted");
        }
        projectRepository.deleteById(id);
    }
}
