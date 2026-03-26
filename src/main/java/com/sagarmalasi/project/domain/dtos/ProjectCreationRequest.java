package com.sagarmalasi.project.domain.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectCreationRequest {
    @NotBlank(message = "Name is Required")
    private String name;

    @NotBlank(message = "A Short Description is needed")
    @Size(min = 50)
    private String description;

    @NotNull(message = "Please Specify Project Start Date")
    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDate plannedStartDate;

    @NotNull(message = "Please Specify Project End Date")
    @Future(message = "End Date Must be ahead of Start Date")
    private LocalDate plannedEndDate;

    @NotNull(message = "Budget Cannot Be null")
   @DecimalMin(value = "10000", inclusive = true, message = "Minimum budget must be 10000")
    private BigDecimal budget;

    @AssertTrue(message = "Planned end date must be after start date")
    public boolean isValidDateRange() {
        if (plannedStartDate == null || plannedEndDate == null) {
            return true; // handled by @NotNull
        }
        return plannedEndDate.isAfter(plannedStartDate);
    }



}
