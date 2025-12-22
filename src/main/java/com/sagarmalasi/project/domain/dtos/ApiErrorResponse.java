package com.sagarmalasi.project.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {
    private int status;
    private String message;
    private List<FieldError> errorList;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FieldError{
        private String field;
        private String message;
    }

}
