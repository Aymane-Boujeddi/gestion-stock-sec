package com.gestion.stock.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standardized error response for all exceptions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private int status;

    private String error;

    private String message;

    private String path;

    /**
     * For validation errors - field-specific errors
     */
    private Object details;
}

