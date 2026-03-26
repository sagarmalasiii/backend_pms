package com.sagarmalasi.project.domain.dtos;

import java.util.UUID;

public record ResourceOverloadResult(
        UUID userId,
        String username,
        boolean overloaded,
        int totalAssignedTasks
        )
{}
