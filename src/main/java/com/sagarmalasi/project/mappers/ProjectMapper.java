package com.sagarmalasi.project.mappers;

import com.sagarmalasi.project.domain.dtos.ProjectCreationRequest;
import com.sagarmalasi.project.domain.dtos.ProjectDto;
import com.sagarmalasi.project.domain.entities.Project;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ProjectMapper {

    ProjectDto toDto(Project project);
    Project toEntity(ProjectCreationRequest projectCreationRequest);
}
