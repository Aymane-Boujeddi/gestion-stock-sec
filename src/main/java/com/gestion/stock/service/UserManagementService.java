package com.gestion.stock.service;

import com.gestion.stock.dto.request.AssignRoleRequestDTO;
import com.gestion.stock.dto.request.ModifyPermissionRequestDTO;
import com.gestion.stock.dto.response.RoleAssignmentResponseDTO;
import com.gestion.stock.dto.response.UserPermissionsResponseDTO;

import java.util.List;

/**
 * Service interface for managing users, roles, and permissions
 * Only accessible by ADMIN role
 */
public interface UserManagementService {

    /**
     * Assign a role to a user
     */
    RoleAssignmentResponseDTO assignRoleToUser(AssignRoleRequestDTO request);

    /**
     * Add or remove a specific permission for a user
     */
    String modifyUserPermission(ModifyPermissionRequestDTO request);

    /**
     * Get all effective permissions for a user
     */
    UserPermissionsResponseDTO getUserPermissions(Long userId);

    /**
     * Get all users with their roles
     */
    List<UserPermissionsResponseDTO> getAllUsers();
}
