package com.sagarmalasi.project.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDto {
    private UUID id;
    private String name;
    private String description;
    private LocalDate plannedEndDate;
    private LocalDate startDate;
    private UUID managerId;


}
