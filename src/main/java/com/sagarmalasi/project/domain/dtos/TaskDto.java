package com.sagarmalasi.project.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDto {
    private UUID id;
    private UUID projectId;
    private String title;
    private String description;
    private String priority;
    private String status;
    private Double estimatedHours;
    private Double actualHours;

}
