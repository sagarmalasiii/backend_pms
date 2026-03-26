package com.sagarmalasi.project.domain.dtos;

import java.util.UUID;

public record DelaySimulationRequest(UUID taskId, int delayDays) {
}
