package com.sagarmalasi.project.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class ProjectCreationRequest {
    @NotBlank(message = "Name is Required")
    private String name;

    @NotBlank(message = "A Short Description is needed")
    @Size(min = 50)
    private String description;

    @NotNull(message = "Please Specify Project Start Date")
    private LocalDate startDate;

    @NotNull(message = "Please Specify Project End Date")
    private LocalDate plannedEndDate;

}
