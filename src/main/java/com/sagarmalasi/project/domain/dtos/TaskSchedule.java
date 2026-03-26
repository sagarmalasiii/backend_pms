package com.sagarmalasi.project.domain.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TaskSchedule {
    private UUID taskId;
    private long duration;

    private long earlyStart;
    private long earlyFinish;

    private long lateStart;
    private long lateFinish;

    private long slack;
    private boolean critical;
}
