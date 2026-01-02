package com.gestion.stock.controller;

import com.gestion.stock.dto.request.AssignRoleRequestDTO;
import com.gestion.stock.dto.request.ModifyPermissionRequestDTO;
import com.gestion.stock.dto.response.RoleAssignmentResponseDTO;
import com.gestion.stock.dto.response.UserPermissionsResponseDTO;
import com.gestion.stock.service.UserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserManagementService userManagementService;


    @PostMapping("/assign-role")
    @PreAuthorize("hasAuthority('ROLE_ASSIGN')")
    public ResponseEntity<RoleAssignmentResponseDTO> assignRole(@Valid @RequestBody AssignRoleRequestDTO request) {
        RoleAssignmentResponseDTO response = userManagementService.assignRoleToUser(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/modify-permission")
    @PreAuthorize("hasAuthority('PERMISSION_MANAGE')")
    public ResponseEntity<String> modifyPermission(@Valid @RequestBody ModifyPermissionRequestDTO request) {
        String message = userManagementService.modifyUserPermission(request);
        return ResponseEntity.ok(message);
    }


    @GetMapping("/{userId}/permissions")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public ResponseEntity<UserPermissionsResponseDTO> getUserPermissions(@PathVariable Long userId) {
        UserPermissionsResponseDTO permissions = userManagementService.getUserPermissions(userId);
        return ResponseEntity.ok(permissions);
    }


    @GetMapping
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public ResponseEntity<List<UserPermissionsResponseDTO>> getAllUsers() {
        List<UserPermissionsResponseDTO> users = userManagementService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
