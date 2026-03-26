package com.sagarmalasi.project.services;


import com.sagarmalasi.project.domain.dtos.ProjectCreationRequest;
import com.sagarmalasi.project.domain.dtos.ProjectDto;
import com.sagarmalasi.project.domain.entities.Project;
import com.sagarmalasi.project.domain.entities.ProjectStatus;
import com.sagarmalasi.project.domain.entities.User;
import com.sagarmalasi.project.mappers.ProjectMapper;
import com.sagarmalasi.project.repositories.ProjectRepository;
import com.sagarmalasi.project.repositories.UserRepository;
import com.sagarmalasi.project.security.SecurityUtils;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserRepository userRepository;

    //Member,Manager adn Admin can view the projects

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


    public ProjectDto getProjectById(UUID projectId) {

        UUID userId = SecurityUtils.getCurrentUserId();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));


        return projectMapper.toDto(project);
    }

    //User whose role is Manager can only create project(no Member)
    @Transactional

    public ProjectDto createProject(@Valid ProjectCreationRequest request) {

        LocalDate plannedStart = request.getPlannedStartDate();
        LocalDate plannedEnd = request.getPlannedEndDate();

        if (plannedStart.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Project start date cannot be in the past");
        }

        if (plannedEnd.isBefore(plannedStart)) {
            throw new IllegalArgumentException("Planned end date cannot be before start date");
        }

        UUID managerId = SecurityUtils.getCurrentUserId();

        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Project project = projectMapper.toEntity(request);
        project.setManager(manager);
        project.setStatus(ProjectStatus.PLANNED);

        return projectMapper.toDto(projectRepository.save(project));
    }



//Manager who has created a project can only delete it(cannot delete others projects)

    @Transactional
    public void deleteProject(UUID id) {

        UUID currentUserId = SecurityUtils.getCurrentUserId();

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        //Checking if the current user(Manager) is actual owner
        if (!project.getManager().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You are not the project manager");
        }

        //If the projects has some tasks linked it cannot deleted(must be project with no tasks)
        if (projectRepository.findAssociatedTasksCount(id) > 0) {
            throw new IllegalStateException("Project has associated tasks; it cannot be deleted");
        }

        projectRepository.delete(project);
    }

}
