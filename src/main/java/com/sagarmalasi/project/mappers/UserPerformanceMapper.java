package com.sagarmalasi.project.mappers;

import com.sagarmalasi.project.domain.dtos.UserPerformanceDto;
import com.sagarmalasi.project.domain.entities.UserPerformance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface UserPerformanceMapper {

    @Mapping(source = "member.id", target = "userId")
    UserPerformanceDto toDto(UserPerformance performance);
}
