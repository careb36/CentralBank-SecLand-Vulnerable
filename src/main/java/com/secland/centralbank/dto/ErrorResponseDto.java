package com.secland.centralbank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {
    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
    private Map<String, String> validationErrors;
}
