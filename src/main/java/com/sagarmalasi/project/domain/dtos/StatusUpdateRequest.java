package com.sagarmalasi.project.domain.dtos;



import com.sagarmalasi.project.domain.entities.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusUpdateRequest {
    @NotNull
    private TaskStatus status;
}
