package com.sagarmalasi.project.mappers;

import com.sagarmalasi.project.domain.dtos.TaskCreationRequest;
import com.sagarmalasi.project.domain.dtos.TaskDto;
import com.sagarmalasi.project.domain.entities.Task;
import com.sagarmalasi.project.domain.entities.TaskAssignment;
import org.mapstruct.*;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface TaskMapper {

        @Mapping(source = "project.id", target = "projectId")
        TaskDto toDto(Task task);

        // After mapping, fill assignee details
        @AfterMapping
        default void afterMapping(Task task, @MappingTarget TaskDto dto) {
                // Find the active assignment
                task.getTaskAssignments().stream()
                        .filter(TaskAssignment::getIsActive)
                        .findFirst()
                        .ifPresent(assignment -> {
                                dto.setAssigneeId(assignment.getMember().getId());
                                dto.setAssigneeUsername(assignment.getMember().getUsername());
                        });
        }

        // ... existing toEntity method ...
        @Mapping(target = "id", ignore = true)
        @Mapping(target = "project", ignore = true)
        @Mapping(target = "createdAt", ignore = true)
        @Mapping(target = "updatedAt", ignore = true)
        @Mapping(target = "taskAssignments", ignore = true)
        Task toEntity(TaskCreationRequest request);
}