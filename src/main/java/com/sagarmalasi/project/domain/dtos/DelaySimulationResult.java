package com.sagarmalasi.project.domain.dtos;

import java.util.UUID;

public record DelaySimulationResult(
        UUID taskId,
        int delayDays,
        int slack,
        boolean critical,
        int projectDelayDays,
        int newProjectDuration
) {
}
