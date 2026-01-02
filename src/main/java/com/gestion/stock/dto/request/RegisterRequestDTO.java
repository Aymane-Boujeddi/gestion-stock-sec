package com.gestion.stock.dto.request;

import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RegisterRequestDTO {
    
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @NotBlank(message = "Username cannot be empty")
    @NotNull(message = "Username is required")
    private String username;


    @Size(min = 6, message = "Password must be at least 6 characters")
    @NotBlank(message = "Password cannot be empty")
    @NotNull(message = "Password is required")
    private String password;

}





