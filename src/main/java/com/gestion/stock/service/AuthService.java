package com.gestion.stock.service;

import com.gestion.stock.dto.request.AuthRequestDTO;
import com.gestion.stock.dto.request.RegisterRequestDTO;
import com.gestion.stock.dto.response.AuthResponseDTO;
import com.gestion.stock.dto.response.RegisterResponseDTO;
import org.springframework.security.core.AuthenticationException;

public interface AuthService {
    AuthResponseDTO handleLogin(AuthRequestDTO authRequestDTO) throws AuthenticationException;
    RegisterResponseDTO handleRegister(RegisterRequestDTO registerRequestDTO);
}
