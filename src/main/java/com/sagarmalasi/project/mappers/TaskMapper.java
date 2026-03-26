package com.sagarmalasi.project.mappers;

import com.sagarmalasi.project.domain.dtos.TaskCreationRequest;
import com.sagarmalasi.project.domain.dtos.TaskDto;
import com.sagarmalasi.project.domain.entities.Task;
import lombok.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.WARN)
public interface TaskMapper {


        // Convert entity → DTO (for API responses)
        @Mapping(source = "project.id", target = "projectId")
        TaskDto toDto(Task task);

        // Convert creation DTO → entity (for creating new tasks)
        @Mapping(target = "id", ignore = true)
        @Mapping(target = "project", ignore = true)
        @Mapping(target = "createdAt", ignore = true)
        @Mapping(target = "updatedAt", ignore = true)

        @Mapping(target = "taskAssignments", ignore = true)


        @Mapping(source = "plannedStartDate", target = "plannedStartDate")
        @Mapping(source = "plannedEndDate", target = "plannedEndDate")

        Task toEntity(TaskCreationRequest request);





}
