package com.gestion.stock.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for modifying a specific permission for a user
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModifyPermissionRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Permission name is required")
    @NotBlank(message = "Permission name cannot be empty")
    private String permissionName;

    @NotNull(message = "Granted status is required")
    private Boolean granted;
}
