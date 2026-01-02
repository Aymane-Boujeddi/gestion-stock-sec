package com.gestion.stock.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.stock.dto.response.ErrorResponseDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        // Check if response is already committed
        if (response.isCommitted()) {
            return;
        }

        String errorMessage = (String) request.getAttribute("error_message");
        if (errorMessage == null) {
            errorMessage = authException.getMessage();
        }

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpServletResponse.SC_UNAUTHORIZED)
                .error("Unauthorized")
                .message(errorMessage != null ? errorMessage : "Authentication required")
                .path(request.getRequestURI())
                .build();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}

