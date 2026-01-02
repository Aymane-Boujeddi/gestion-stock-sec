package com.gestion.stock.service.impl;

import com.gestion.stock.dto.request.AssignRoleRequestDTO;
import com.gestion.stock.dto.request.ModifyPermissionRequestDTO;
import com.gestion.stock.dto.response.RoleAssignmentResponseDTO;
import com.gestion.stock.dto.response.UserPermissionsResponseDTO;
import com.gestion.stock.entity.Permission;
import com.gestion.stock.entity.RoleApp;
import com.gestion.stock.entity.User;
import com.gestion.stock.entity.UserPermission;
import com.gestion.stock.repository.PermissionRepository;
import com.gestion.stock.repository.RoleAppRepository;
import com.gestion.stock.repository.UserPermissionRepository;
import com.gestion.stock.repository.UserRepository;
import com.gestion.stock.service.AuditService;
import com.gestion.stock.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;
    private final RoleAppRepository roleAppRepository;
    private final PermissionRepository permissionRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final AuditService auditService;

    /**
     * Assign a role to a user
     */
    @Override
    @Transactional
    public RoleAssignmentResponseDTO assignRoleToUser(AssignRoleRequestDTO request) {
        // Find user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));

        // Find role
        RoleApp role = roleAppRepository.findByName(request.getRole())
                .orElseThrow(() -> new RuntimeException("Role not found: " + request.getRole()));

        // Assign role to user
        user.setRole(role);
        userRepository.save(user);

        // Get all permissions for this role
        List<Permission> rolePermissions = roleAppRepository.findPermissionsByRole(request.getRole());

        // Convert to permission names
        List<String> permissionNames = rolePermissions.stream()
                .map(Permission::getName)
                .sorted()
                .toList();

        // Journaliser l'assignation de rôle (audit)
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        auditService.logAction(
            currentUser, 
            "ASSIGN_ROLE", 
            "Rôle " + request.getRole() + " assigné à " + user.getUsername()
        );

        // Build response
        return RoleAssignmentResponseDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .permissions(permissionNames)
                .message("Role " + request.getRole() + " assigned successfully to user " + user.getUsername())
                .build();
    }

    /**
     * Helper method to get role description
     */
    private String getRoleDescription(String roleName) {
        return switch (roleName) {
            case "ADMIN" -> "Administrateur système - Accès complet";
            case "RESPONSABLE_ACHATS" -> "Responsable des achats - Gestion des commandes et fournisseurs";
            case "MAGASINIER" -> "Magasinier - Gestion du stock et des bons de sortie";
            case "CHEF_ATELIER" -> "Chef d'atelier - Consultation et demandes de sortie";
            default -> "Rôle personnalisé";
        };
    }

   
    @Override
    @Transactional
    public String modifyUserPermission(ModifyPermissionRequestDTO request) {
        // Find user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));

        // Find permission
        Permission permission = permissionRepository.findByName(request.getPermissionName())
                .orElseThrow(() -> new RuntimeException("Permission not found: " + request.getPermissionName()));

        // Check if user already has this permission override
        Optional<UserPermission> existingPermission = user.getUserPermissions().stream()
                .filter(up -> up.getPermission().getId().equals(permission.getId()))
                .findFirst();

        if (existingPermission.isPresent()) {
            // Update existing permission
            UserPermission userPerm = existingPermission.get();
            userPerm.setGranted(request.getGranted());
            userPermissionRepository.save(userPerm);
        } else {
            // Create new permission override
            UserPermission newPermission = UserPermission.builder()
                    .user(user)
                    .permission(permission)
                    .granted(request.getGranted())
                    .build();
            userPermissionRepository.save(newPermission);
        }

        // Journaliser la modification de permission (audit)
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        auditService.logPermissionChange(
            currentUser,
            user.getUsername(),
            request.getPermissionName(),
            request.getGranted()
        );

        String action = request.getGranted() ? "granted to" : "removed from";
        return "Permission " + request.getPermissionName() + " " + action + " user " + user.getUsername();
    }

    /**
     * Get all effective permissions for a user
     */
    @Override
    @Transactional(readOnly = true)
    public UserPermissionsResponseDTO getUserPermissions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Calculate effective permissions (same logic as CustomUserDetailsService)
        Set<String> effectivePermissions = new HashSet<>();

        // Add role default permissions
        if (user.getRole() != null && user.getRole().getDefaultPermissions() != null) {
            for (Permission perm : user.getRole().getDefaultPermissions()) {
                effectivePermissions.add(perm.getName());
            }
        }

        // Apply user-specific overrides
        List<UserPermissionsResponseDTO.PermissionOverride> overrides = new ArrayList<>();
        if (user.getUserPermissions() != null) {
            for (UserPermission up : user.getUserPermissions()) {
                if (up.getPermission() == null) continue;

                String permName = up.getPermission().getName();
                overrides.add(UserPermissionsResponseDTO.PermissionOverride.builder()
                        .permissionName(permName)
                        .granted(up.isGranted())
                        .build());

                if (up.isGranted()) {
                    effectivePermissions.add(permName);
                } else {
                    effectivePermissions.remove(permName);
                }
            }
        }

        return UserPermissionsResponseDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .roleName(user.getRole() != null ? user.getRole().getName().name() : "NO_ROLE")
                .effectivePermissions(new ArrayList<>(effectivePermissions))
                .customOverrides(overrides)
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public List<UserPermissionsResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> getUserPermissions(user.getId()))
                .toList();
    }
}
