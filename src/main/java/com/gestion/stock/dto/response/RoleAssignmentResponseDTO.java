package com.gestion.stock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for role assignment operations
 * Returns the updated user information with role and permissions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleAssignmentResponseDTO {

    private Long userId;

    private String username;


    private List<String> permissions;

    private String message;


}

