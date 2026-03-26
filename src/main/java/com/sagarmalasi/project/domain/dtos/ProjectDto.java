package com.sagarmalasi.project.domain.dtos;

import com.sagarmalasi.project.domain.entities.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    private LocalDate plannedStartDate;
    private UUID managerId;
    private ProjectStatus status;
    private LocalDate actualEndDate;
    private BigDecimal budget;


}
