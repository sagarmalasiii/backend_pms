package com.sagarmalasi.project.domain.dtos;

import com.sagarmalasi.project.domain.entities.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RiskAssessmentDto {
    private UUID id;
    private UUID taskId;
    private Double predictedCompletionHours;
    private Double estimatedHours;
    private RiskLevel riskLevel;
    private String modelVersion;
    private LocalDateTime assessedAt;
}
