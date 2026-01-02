package com.gestion.stock.security;

import com.gestion.stock.entity.User;
import com.gestion.stock.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Converts a validated Keycloak JWT into a Spring Security Authentication.
 * 
 * Flow:
 * 1. Spring validates the token (signature, expiry, issuer) BEFORE this runs
 * 2. We extract the user identity from the token
 * 3. We find or create the user in our DB
 * 4. We load permissions from DB (empty if no role assigned yet)
 * 5. Admin can later assign a role via your management endpoints
 */
@Component
@RequiredArgsConstructor
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String PROVIDER = "KEYCLOAK";

    private final UserRepository userRepository;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // Step 1: Get stable user identifier from Keycloak
        String sub = jwt.getSubject();
        
        // Step 2: Get human-readable username from token (for display in DB)
        String tokenUsername = jwt.getClaimAsString("preferred_username");
        String username = (tokenUsername != null && !tokenUsername.isBlank()) 
                ? tokenUsername 
                : "kc_" + sub.substring(0, Math.min(8, sub.length()));

        // Step 3: Find existing user or create new one
        User user = userRepository.findByAuthProviderAndClientIdSubWithPermissions(PROVIDER, sub)
                .orElseGet(() -> createKeycloakUser(sub, username));

        // Step 4: Build authorities directly (avoid second DB query that might fail)
        List<GrantedAuthority> authorities = buildAuthorities(user);

        // Step 5: Return authentication with user details and permissions
        CustomUserDetails principal = new CustomUserDetails(user, authorities);
        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

    /**
     * Creates a new user in DB for first-time Keycloak login.
     * User starts with no role (admin must assign one).
     */
    private User createKeycloakUser(String sub, String preferredUsername) {
        String username = makeUsernameUnique(preferredUsername);

        User user = User.builder()
                .username(username)
                .password(null)           // No password for OAuth users
                .authProvider(PROVIDER)   // Mark as Keycloak user
                .clientIdSub(sub)         // Link to Keycloak identity
                .role(null)               // No role = no permissions (admin assigns later)
                .build();

        return userRepository.save(user);
    }

    /**
     * Ensures username is unique in DB.
     */
    private String makeUsernameUnique(String base) {
        if (!userRepository.existsByUsername(base)) {
            return base;
        }
        return base + "_" + System.currentTimeMillis();
    }

    /**
     * Builds authorities directly from the user entity.
     * Uses the same logic as CustomUserDetailsService but without extra DB query.
     */
    private List<GrantedAuthority> buildAuthorities(User user) {
        if (user.getRole() == null) {
            return List.of(); // No role = no permissions
        }
        
        // Get role default permissions
        List<String> permissionNames = new ArrayList<>();
        if (user.getRole().getDefaultPermissions() != null) {
            user.getRole().getDefaultPermissions().forEach(permission -> 
                permissionNames.add(permission.getName())
            );
        }
        
        // Apply user-specific permission overrides
        Set<String> finalPermissions = new HashSet<>(permissionNames);
        if (user.getUserPermissions() != null) {
            user.getUserPermissions().forEach(userPerm -> {
                if (userPerm.getPermission() != null) {
                    String permName = userPerm.getPermission().getName();
                    if (userPerm.isGranted()) {
                        finalPermissions.add(permName);
                    } else {
                        finalPermissions.remove(permName);
                    }
                }
            });
        }
        
        return finalPermissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}