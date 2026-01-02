package com.gestion.stock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO containing user's effective permissions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPermissionsResponseDTO {

    private Long userId;
    private String username;
    private String roleName;
    private List<String> effectivePermissions;
    private List<PermissionOverride> customOverrides;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermissionOverride {
        private String permissionName;
        private boolean granted; // true = added, false = removed
    }
}
