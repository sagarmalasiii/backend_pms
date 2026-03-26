package com.sagarmalasi.project.domain.dtos;

import com.sagarmalasi.project.domain.entities.Priority;
import com.sagarmalasi.project.domain.entities.TaskStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskCreationRequest {
    @NotNull(message = "Project Id is required")
    private UUID projectId;
    @NotBlank(message = "Title is Required !!!")
    private String title;

    @NotBlank(message = "A short Description for Task is required")
    @Size(min = 20, max = 1000)
    private String description;

    @NotNull(message = "Please set Task Priority")
    private Priority priority;



    @NotNull(message = "You need start date")
    @FutureOrPresent(message = "Start date cannot be in past")
    private LocalDate plannedStartDate;

    @NotNull(message = "You need end date")
    @Future(message = "End date must be in future")
    private LocalDate plannedEndDate;

    @AssertTrue(message = "Planned end date must be after start date")
    public boolean isValidDateRange() {
        if (plannedStartDate == null || plannedEndDate == null) {
            return true; // handled by @NotNull
        }
        return plannedEndDate.isAfter(plannedStartDate);
    }

}
