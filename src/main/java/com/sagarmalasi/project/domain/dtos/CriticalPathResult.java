package com.sagarmalasi.project.domain.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Builder
@Data
public class CriticalPathResult {
    private List<TaskSchedule> taskSchedules;
    private List<UUID> criticalPathTaskIds;
    private long projectDurationDays;
}
