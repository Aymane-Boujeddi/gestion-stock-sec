package com.gestion.stock.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.stock.dto.response.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Handles authorization errors when an authenticated user
 * tries to access a resource they don't have permission for
 * This is triggered when:
 * - User is authenticated but lacks required permission
 * - @PreAuthorize annotation check fails
 * - hasAuthority() check fails
 */
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {


        // Build standardized error response
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpServletResponse.SC_FORBIDDEN)
                .error("Forbidden")
                .message("You don't have permission to access this resource")
                .path(request.getRequestURI())
                .build();

        // Set response headers
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // Write JSON response
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}

