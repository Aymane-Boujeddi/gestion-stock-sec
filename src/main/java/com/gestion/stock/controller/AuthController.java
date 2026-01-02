package com.gestion.stock.controller;


import com.gestion.stock.dto.request.AuthRequestDTO;
import com.gestion.stock.dto.request.RegisterRequestDTO;
import com.gestion.stock.dto.response.AuthResponseDTO;
import com.gestion.stock.dto.response.RegisterResponseDTO;
import com.gestion.stock.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO authRequestDTO) {
        return ResponseEntity.ok(authService.handleLogin(authRequestDTO));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        return ResponseEntity.ok(authService.handleRegister(registerRequestDTO));
    }
}
