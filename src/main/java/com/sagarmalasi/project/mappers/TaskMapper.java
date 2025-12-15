package com.sagarmalasi.project.mappers;

import com.sagarmalasi.project.domain.dtos.TaskDto;
import com.sagarmalasi.project.domain.entities.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.WARN)
public interface TaskMapper {
    @Mapping(source = "project.id",target = "projectId")
    TaskDto toDto(Task task);
}
