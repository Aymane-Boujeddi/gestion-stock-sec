package com.gestion.stock.security;

import com.gestion.stock.entity.Permission;
import com.gestion.stock.entity.User;
import com.gestion.stock.entity.UserPermission;
import com.gestion.stock.repository.RoleAppRepository;
import com.gestion.stock.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleAppRepository roleAppRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getRole() != null) {

        List<Permission> effectivePermissions = getEffectivePermissions(user);

        authorities = effectivePermissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toList());
        }

        return new CustomUserDetails(user, authorities);
    }

    private List<Permission> getEffectivePermissions(User user) {


        List<Permission> rolePermissions = getRolePermissions(user);

        if (hasUserPermissionOverrides(user)) {
            return applyUserPermissionOverrides(rolePermissions, user.getUserPermissions());
        }

        return rolePermissions;
    }

    private List<Permission> getRolePermissions(User user) {
        if (user.getRole() != null && user.getRole().getName() != null) {
            return roleAppRepository.findPermissionsByRole(user.getRole().getName());
        }
        return List.of();
    }

    private boolean hasUserPermissionOverrides(User user) {
        return user.getUserPermissions() != null && !user.getUserPermissions().isEmpty();
    }

    private List<Permission> applyUserPermissionOverrides(
            List<Permission> rolePermissions,
            List<UserPermission> userPermissions) {

        Set<String> permissionNames = rolePermissions.stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());

        for (UserPermission userPermission : userPermissions) {
            if (userPermission.getPermission() == null) continue;

            String permName = userPermission.getPermission().getName();

            if (userPermission.isGranted()) {
                permissionNames.add(permName);
            } else {
                permissionNames.remove(permName);
            }
        }

        Set<Permission> allPermissions = new HashSet<>(rolePermissions);
        userPermissions.stream()
                .map(UserPermission::getPermission)
                .filter(permission -> permission != null)
                .forEach(allPermissions::add);

        return allPermissions.stream()
                .filter(permission -> permissionNames.contains(permission.getName()))
                .collect(Collectors.toList());
    }
}
