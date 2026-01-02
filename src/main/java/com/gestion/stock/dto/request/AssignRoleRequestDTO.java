package com.gestion.stock.dto.request;

import com.gestion.stock.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for assigning a role to a user
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignRoleRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Role is required")
    private Role role;
}
