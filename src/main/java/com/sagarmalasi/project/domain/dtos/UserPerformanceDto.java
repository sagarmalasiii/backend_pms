package com.sagarmalasi.project.domain.dtos;

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
public class UserPerformanceDto {
    private UUID userId;
    private Integer totalTaskCompleted;
    private Double avgDelayRatio;
    private LocalDateTime lastCalculatedAt;
}
