package com.sagarmalasi.project.mappers;

import com.sagarmalasi.project.domain.dtos.LoginRequest;
import com.sagarmalasi.project.domain.dtos.RegisterRequest;
import com.sagarmalasi.project.domain.dtos.UserDto;
import com.sagarmalasi.project.domain.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {


    User toEntity(RegisterRequest registerRequest);
    UserDto toDto(User user);


}
