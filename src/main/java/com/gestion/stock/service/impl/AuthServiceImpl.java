package com.gestion.stock.service.impl;

import com.gestion.stock.dto.request.AuthRequestDTO;
import com.gestion.stock.dto.request.RegisterRequestDTO;
import com.gestion.stock.dto.response.AuthResponseDTO;
import com.gestion.stock.dto.response.RegisterResponseDTO;
import com.gestion.stock.entity.User;
import com.gestion.stock.exception.DuplicateResourceException;
import com.gestion.stock.exception.RoleNotAssignedException;
import com.gestion.stock.repository.UserRepository;
import com.gestion.stock.service.AuthService;
import com.gestion.stock.service.AuditService;
import com.gestion.stock.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

    @Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final AuditService auditService;

    @Override
    @Transactional
    public AuthResponseDTO handleLogin(AuthRequestDTO authRequestDTO) throws AuthenticationException {
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequestDTO.getUsername(),
                        authRequestDTO.getPassword()
                )
        );

        String username = authentication.getName();

        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == null) {
            throw new RoleNotAssignedException(
                "Your account is pending activation. Please contact an administrator to assign you a role."
            );
        }

        String token = jwtUtil.generateToken(username);

        auditService.logLogin(username);

        return AuthResponseDTO.builder()
                .message("Login successful")
                .username(username)
                .token(token)
                .build();
    }

    @Override
    @Transactional
    public RegisterResponseDTO handleRegister(RegisterRequestDTO registerRequestDTO) {
        
        if (userRepository.existsByUsername(registerRequestDTO.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + registerRequestDTO.getUsername());
        }

        String encodedPassword = passwordEncoder.encode(registerRequestDTO.getPassword());

        User newUser = User.builder()
                .username(registerRequestDTO.getUsername())
                .password(encodedPassword)
                .authProvider("Local")
                .role(null)
                .build();

        User savedUser = userRepository.save(newUser);

        auditService.logAction(savedUser.getUsername(), "REGISTER", "New user registered");

        return RegisterResponseDTO.builder()
                .message("User registered successfully. Please wait for admin to assign your role.")
                .username(savedUser.getUsername())
                .build();
    }
}