package com.sagarmalasi.project.mappers;

import com.sagarmalasi.project.domain.dtos.RiskAssessmentDto;
import com.sagarmalasi.project.domain.entities.RiskAssessment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface RiskAssessmentMapper {

    @Mapping(source = "task.id", target = "taskId")
    RiskAssessmentDto toDto(RiskAssessment assessment);
}
