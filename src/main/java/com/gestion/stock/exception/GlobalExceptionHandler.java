package com.gestion.stock.exception;


import com.gestion.stock.dto.response.ErrorResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for Spring MVC layer exceptions
 * Note: Authentication/Authorization exceptions are handled separately
 * by JwtAuthenticationEntryPoint and CustomAccessDeniedHandler
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsuffissientStockException.class)
    public ResponseEntity<ErrorResponseDTO> handleInsufissientStockException(
            InsuffissientStockException exception,
            HttpServletRequest request
    ) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Insufficient Stock")
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntegrityViolationException(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Data Integrity Violation")
                .message("Database constraint violation. Please check your data.")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.put(error.getField(), error.getDefaultMessage())
        );

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Input validation failed")
                .path(request.getRequestURI())
                .details(validationErrors)
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponseDTO> handleDuplicateResourceException(
            DuplicateResourceException exception,
            HttpServletRequest request
    ) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Duplicate Resource")
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleEntityNotFoundException(
            EntityNotFoundException exception,
            HttpServletRequest request
    ) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RoleNotAssignedException.class)
    public ResponseEntity<ErrorResponseDTO> handleRoleNotAssignedException(
            RoleNotAssignedException exception,
            HttpServletRequest request
    ) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("Role Not Assigned")
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidTokenException(
            InvalidTokenException exception,
            HttpServletRequest request
    ) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Invalid Token")
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InsufficientPermissionException.class)
    public ResponseEntity<ErrorResponseDTO> handleInsufficientPermissionException(
            InsufficientPermissionException exception,
            HttpServletRequest request
    ) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("Insufficient Permission")
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    /**
     * Generic exception handler for unexpected errors
     * Note: AccessDeniedException and AuthenticationException are excluded
     * as they are handled by Spring Security's exception handlers
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(
            Exception exception,
            HttpServletRequest request
    ) {
        // Don't handle Spring Security exceptions here
        if (exception instanceof org.springframework.security.access.AccessDeniedException ||
            exception instanceof org.springframework.security.core.AuthenticationException) {
            throw (RuntimeException) exception;
        }

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred: " + exception.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
