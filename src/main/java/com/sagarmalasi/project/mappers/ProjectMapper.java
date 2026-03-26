package com.sagarmalasi.project.mappers;

import com.sagarmalasi.project.domain.dtos.ProjectCreationRequest;
import com.sagarmalasi.project.domain.dtos.ProjectDto;
import com.sagarmalasi.project.domain.entities.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ProjectMapper {

    @Mapping(source = "manager.id",target = "managerId")
    ProjectDto toDto(Project project);
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "manager",ignore = true)
    @Mapping(target = "tasks",ignore = true)
    @Mapping(target = "actualEndDate",ignore = true)
    @Mapping(target = "createdAt",ignore = true)
    @Mapping(target = "updatedAt",ignore = true)
    Project toEntity(ProjectCreationRequest projectCreationRequest);
}
