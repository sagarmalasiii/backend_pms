package com.sagarmalasi.project.domain.entities;

public enum ProjectStatus {
    PLANNED,        // Created, work not started
    ACTIVE,         // Tasks in progress
    ON_HOLD,        // Temporarily paused
    COMPLETED,      // All work finished
    CANCELLED       // Stopped permanently
}

