package com.sagarmalasi.project.domain.dtos;

import com.sagarmalasi.project.domain.entities.Priority;
import com.sagarmalasi.project.domain.entities.TaskStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @NotNull(message = "Specify the Estimated Hours to Complete !!!")
    @Min(value = 1)
    private Integer estimatedHours;

}
