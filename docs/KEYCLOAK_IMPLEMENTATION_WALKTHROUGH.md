# Keycloak Integration - Complete Step-by-Step Walkthrough

**Project:** Gestion des Approvisionnements (Security)  
**Current State:** Legacy JWT authentication with HS256  
**Goal:** Add Keycloak OAuth2/OIDC support while keeping legacy JWT  
**Difficulty:** Beginner-friendly with detailed explanations

---

## 📋 Prerequisites Checklist

Before starting:

- [ ] Git installed (for version control)
- [ ] MySQL running on localhost:3306
- [ ] Database `gestion_stock_db` exists
- [ ] Maven installed (for building)
- [ ] Keycloak instance (Docker or standalone) - we'll setup later

---

## 🎯 What We'll Build

```
┌─────────────────────────────────────────────────────────────┐
│                     Your Spring Boot API                     │
│                                                              │
│  ┌────────────┐           ┌─────────────────┐              │
│  │  Legacy    │           │   Keycloak      │              │
│  │  JWT Auth  │           │   OAuth2 Auth   │              │
│  └────────────┘           └─────────────────┘              │
│        │                           │                        │
│        └───────────┬───────────────┘                        │
│                    ▼                                        │
│           ┌─────────────────┐                               │
│           │  DB User Table  │                               │
│           │  (unified)      │                               │
│           └─────────────────┘                               │
└─────────────────────────────────────────────────────────────┘
```

---

## 📂 Project Structure Overview

Your current base package: `com.gestion.stock`

Files we'll modify/create:

```
src/main/
├── java/com/gestion/stock/
│   ├── entity/
│   │   └── User.java                              [MODIFY]
│   ├── repository/
│   │   └── UserRepository.java                    [MODIFY]
│   ├── security/
│   │   ├── JwtAuthenticationFilter.java           [MODIFY]
│   │   ├── SecurityConfig.java                    [MODIFY]
│   │   ├── TokenClassifier.java                   [CREATE]
│   │   └── KeycloakJwtAuthenticationConverter.java [CREATE]
│   └── util/
└── resources/
    ├── application.properties                      [MODIFY]
    └── db/changelog/
        ├── db.changelog-master.xml                [MODIFY]
        └── V2_Add_Keycloak_Support.yaml           [CREATE]
```

---

# PHASE 1: Database Preparation

## Step 1.1: Update User Entity

**File:** `src/main/java/com/gestion/stock/entity/User.java`

**Current state:**

```java
@Column(nullable = false)
private String password;
```

**Change to:**

```java
@Column(nullable = true)  // Allow null for Keycloak users
private String password;

@Column(name = "auth_provider", nullable = false)
@Builder.Default
private String authProvider = "LOCAL";  // LOCAL or KEYCLOAK

@Column(name = "external_subject", unique = true)
private String externalSubject;  // Keycloak 'sub' claim
```

**Full updated User.java:**

```java
package com.gestion.stock.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = true)  // ← Changed from nullable = false
    private String password;

    // ↓ NEW FIELDS
    @Column(name = "auth_provider", nullable = false)
    @Builder.Default
    private String authProvider = "LOCAL";

    @Column(name = "external_subject", unique = true)
    private String externalSubject;
    // ↑ NEW FIELDS

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private RoleApp role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserPermission> userPermissions = new ArrayList<>();
}
```

**Why these changes:**

- `password` nullable: Keycloak users authenticate externally, don't need local password
- `authProvider`: distinguish "LOCAL" (your login) vs "KEYCLOAK"
- `externalSubject`: stores Keycloak's stable user ID (`sub` claim)

---

## Step 1.2: Create Liquibase Migration

**File:** `src/main/resources/db/changelog/V2_Add_Keycloak_Support.yaml`

**Create this new file:**

```yaml
databaseChangeLog:
  - changeSet:
      id: add-keycloak-support-to-users
      author: security-team
      changes:
        # Make password nullable
        - dropNotNullConstraint:
            tableName: users
            columnName: password
            columnDataType: VARCHAR(255)

        # Add auth_provider column
        - addColumn:
            tableName: users
            columns:
              - column:
                  name: auth_provider
                  type: VARCHAR(20)
                  constraints:
                    nullable: false
                  defaultValue: LOCAL

        # Add external_subject column
        - addColumn:
            tableName: users
            columns:
              - column:
                  name: external_subject
                  type: VARCHAR(255)
                  constraints:
                    nullable: true
                    unique: true

      rollback:
        - dropColumn:
            tableName: users
            columnName: external_subject
        - dropColumn:
            tableName: users
            columnName: auth_provider
        - addNotNullConstraint:
            tableName: users
            columnName: password
            columnDataType: VARCHAR(255)
```

**Why:**

- Existing users get `auth_provider='LOCAL'` automatically
- New Keycloak users will have `password=null`, `auth_provider='KEYCLOAK'`
- Rollback included for safety

---

## Step 1.3: Include Migration in Master Changelog

**File:** `src/main/resources/db/changelog/db.changelog-master.xml`

**Add this line after `User.yaml`:**

```xml
<include file="db/changelog/V2_Add_Keycloak_Support.yaml"/>
```

**Full file should look like:**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
    xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
    http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.20.xsd">

    <include file="db/changelog/Fournisseurs.xml" />
    <include file="db/changelog/Produit.xml" />
    <include file="db/changelog/Commande.yaml"/>
    <include file="db/changelog/DetailsCommande.yaml"/>
    <include file="db/changelog/Stock.yaml"/>
    <include file="db/changelog/MouvementStocks.yaml"/>
    <include file="db/changelog/BonSortie.yaml"/>
    <include file="db/changelog/BonSortieItem.yaml"/>
    <include file="db/changelog/RoleApp.yaml"/>
    <include file="db/changelog/Permission.yaml"/>
    <include file="db/changelog/RoleDefaultPermissions.yaml"/>
    <include file="db/changelog/User.yaml"/>
    <include file="db/changelog/V2_Add_Keycloak_Support.yaml"/>  <!-- ← ADD THIS -->
    <include file="db/changelog/UserPermission.yaml"/>
    <include file="db/changelog/Audit.yaml"/>

</databaseChangeLog>
```

---

## Step 1.4: Update UserRepository

**File:** `src/main/java/com/gestion/stock/repository/UserRepository.java`

**Add this method:**

```java
Optional<User> findByAuthProviderAndExternalSubject(String authProvider, String externalSubject);
```

**Full updated repository:**

```java
package com.gestion.stock.repository;

import com.gestion.stock.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.role r " +
           "LEFT JOIN FETCH u.userPermissions up " +
           "LEFT JOIN FETCH up.permission " +
           "WHERE u.username = :username")
    Optional<User> findUserByUsername(@Param("username") String username);

    boolean existsByUsername(String username);

    // ↓ NEW METHOD
    Optional<User> findByAuthProviderAndExternalSubject(String authProvider, String externalSubject);
}
```

---

## ✅ TEST PHASE 1

**Before proceeding, verify DB changes work:**

```bash
# 1. Build the project
mvn clean install -DskipTests

# 2. Run the application
mvn spring-boot:run

# 3. Check logs for Liquibase
# You should see: "Successfully applied 1 change(s) to gestion_stock_db"

# 4. Check database
mysql -u root -p097680
USE gestion_stock_db;
DESCRIBE users;
# You should see: auth_provider, external_subject columns
```

**Expected output:**

```
+------------------+--------------+------+-----+---------+
| Field            | Type         | Null | Key | Default |
+------------------+--------------+------+-----+---------+
| password         | varchar(255) | YES  |     | NULL    |  ← NOW NULLABLE
| auth_provider    | varchar(20)  | NO   |     | LOCAL   |  ← NEW
| external_subject | varchar(255) | YES  | UNI | NULL    |  ← NEW
+------------------+--------------+------+-----+---------+
```

**Test existing login still works:**

```bash
# POST to /gestionStock/api/v1/auth/login with existing user
# Should return JWT token as before
```

✅ **Commit:** `git commit -m "feat: add Keycloak support columns to User entity"`

---

# PHASE 2: Token Classification

## Step 2.1: Create TokenClassifier

**File:** `src/main/java/com/gestion/stock/security/TokenClassifier.java` (NEW)

**Purpose:** Detect if a JWT is from Keycloak or your app (without validating signature)

```java
package com.gestion.stock.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
@Slf4j
public class TokenClassifier {

    @Value("${security.keycloak.issuer:}")
    private String keycloakIssuer;

    /**
     * Checks if a JWT token is from Keycloak by reading the 'iss' claim
     * Does NOT validate signature - just classification
     */
    public boolean isKeycloakToken(String jwt) {
        if (keycloakIssuer == null || keycloakIssuer.isBlank()) {
            return false; // Keycloak not configured
        }

        try {
            Map<String, Object> claims = decodeJwtPayload(jwt);
            Object iss = claims.get("iss");

            boolean isKeycloak = iss != null && keycloakIssuer.equals(iss.toString());
            log.debug("Token classified as: {}", isKeycloak ? "KEYCLOAK" : "LEGACY");

            return isKeycloak;
        } catch (Exception e) {
            log.debug("Failed to classify token, treating as legacy: {}", e.getMessage());
            return false; // Safe default: treat as legacy
        }
    }

    /**
     * Decode JWT payload without validating signature
     * JWT format: header.payload.signature
     */
    private Map<String, Object> decodeJwtPayload(String jwt) throws Exception {
        String[] parts = jwt.split("\\.");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid JWT format");
        }

        // Decode Base64URL payload (second part)
        byte[] decodedBytes = Base64.getUrlDecoder().decode(parts[1]);
        String json = new String(decodedBytes, StandardCharsets.UTF_8);

        // Parse JSON
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
    }
}
```

**Explanation:**

- Reads only the `iss` (issuer) claim from JWT payload
- Doesn't validate signature (that's Spring's job later)
- Safe fallback: if can't classify, treats as legacy

---

## Step 2.2: Update JwtAuthenticationFilter

**File:** `src/main/java/com/gestion/stock/security/JwtAuthenticationFilter.java`

**Add TokenClassifier and skip logic:**

```java
package com.gestion.stock.security;

import com.gestion.stock.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final TokenClassifier tokenClassifier;  // ← NEW

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String token = null;
        String username = null;

        try {
            if (header != null && header.startsWith("Bearer ")) {
                token = header.substring(7);

                // ↓ NEW: Skip Keycloak tokens
                if (tokenClassifier.isKeycloakToken(token)) {
                    filterChain.doFilter(request, response);
                    return; // Let OAuth2 Resource Server handle it
                }
                // ↑ NEW

                username = jwtUtil.extractUsername(token);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.isTokenValid(token, username)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/gestionStock/api/v1/auth/");
    }
}
```

**What changed:**

- Inject `TokenClassifier`
- Before parsing, check if token is Keycloak
- If Keycloak, skip this filter (let OAuth2 Resource Server handle it)

---

## ✅ TEST PHASE 2

```bash
# Rebuild and run
mvn clean install -DskipTests
mvn spring-boot:run

# Test legacy login still works
curl -X POST http://localhost:8080/gestionStock/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"youruser","password":"yourpass"}'

# Should return JWT as before
```

✅ **Commit:** `git commit -m "feat: add token classifier to distinguish Keycloak vs legacy JWT"`

---

# PHASE 3: Dependencies & Configuration

## Step 3.1: Add OAuth2 Resource Server Dependency

**File:** `pom.xml`

**Add this dependency (you have oauth2-client, but need resource-server):**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

**Insert it after your oauth2-client dependency:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

---

## Step 3.2: Add Keycloak Configuration

**File:** `src/main/resources/application.properties`

**Add these lines at the end:**

```properties
# Keycloak OAuth2 Configuration
security.keycloak.enabled=false
security.keycloak.issuer=http://localhost:8180/realms/gestion-stock
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8180/realms/gestion-stock
```

**Explanation:**

- `security.keycloak.enabled`: Feature flag (start with false for safety)
- `security.keycloak.issuer`: Used by TokenClassifier
- `spring.security.oauth2.resourceserver.jwt.issuer-uri`: Spring fetches JWKS from here

**Note:** Change `localhost:8180` and `gestion-stock` to match your Keycloak setup

---

## Step 3.3: Reload Dependencies

```bash
mvn clean install -DskipTests
```

✅ **Commit:** `git commit -m "feat: add OAuth2 resource server dependency and config"`

---

# PHASE 4: Keycloak User Provisioning

## Step 4.1: Create KeycloakJwtAuthenticationConverter

**File:** `src/main/java/com/gestion/stock/security/KeycloakJwtAuthenticationConverter.java` (NEW)

**Purpose:** Convert validated Keycloak JWT → local User + permissions

```java
package com.gestion.stock.security;

import com.gestion.stock.entity.User;
import com.gestion.stock.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // Extract claims from Keycloak token
        String sub = jwt.getSubject();  // Stable Keycloak user ID
        String preferredUsername = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");

        log.info("Processing Keycloak token for sub: {}, username: {}", sub, preferredUsername);

        // Find or create local user
        User user = userRepository.findByAuthProviderAndExternalSubject("KEYCLOAK", sub)
                .orElseGet(() -> {
                    log.info("First-time Keycloak user, provisioning: {}", preferredUsername);
                    return provisionUser(sub, preferredUsername, email);
                });

        // Load permissions using existing logic
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());

        log.info("Keycloak user authenticated: username={}, authorities={}",
                 user.getUsername(), userDetails.getAuthorities());

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                "N/A",  // No credentials for Keycloak users
                userDetails.getAuthorities()
        );
    }

    /**
     * Create a new local user for a Keycloak identity (JIT provisioning)
     */
    private User provisionUser(String sub, String preferredUsername, String email) {
        String username = chooseUniqueUsername(preferredUsername, email, sub);

        User newUser = User.builder()
                .username(username)
                .password(null)  // No local password
                .authProvider("KEYCLOAK")
                .externalSubject(sub)
                .role(null)  // Pending role assignment (matches your existing flow)
                .build();

        User savedUser = userRepository.save(newUser);
        log.info("Created new Keycloak user: id={}, username={}, externalSubject={}",
                 savedUser.getId(), savedUser.getUsername(), sub);

        return savedUser;
    }

    /**
     * Generate a unique username, handling collisions
     */
    private String chooseUniqueUsername(String preferredUsername, String email, String sub) {
        // Prefer preferredUsername, fallback to email, then keycloak_<sub>
        String baseUsername = (preferredUsername != null && !preferredUsername.isBlank())
                ? preferredUsername
                : (email != null && !email.isBlank())
                    ? email.split("@")[0]  // Use email prefix
                    : "keycloak_" + sub.substring(0, Math.min(8, sub.length()));

        // Handle collisions by appending suffix
        String finalUsername = baseUsername;
        int suffix = 1;
        while (userRepository.existsByUsername(finalUsername)) {
            finalUsername = baseUsername + "_" + suffix;
            suffix++;
            if (suffix > 100) {
                // Safety: fallback to sub if too many collisions
                finalUsername = "kc_" + sub;
                break;
            }
        }

        return finalUsername;
    }
}
```

**Key points:**

- Reuses your `CustomUserDetailsService` for permission loading
- New users get `role=null` (pending admin approval, like your existing register flow)
- Handles username collisions gracefully
- Logs all provisioning for debugging

---

## ✅ TEST PHASE 4 (Without Keycloak yet)

```bash
# Just verify it compiles
mvn clean compile

# Should succeed with no errors
```

✅ **Commit:** `git commit -m "feat: add Keycloak JWT to User converter with JIT provisioning"`

---

# PHASE 5: Wire Everything Together

## Step 5.1: Update SecurityConfig

**File:** `src/main/java/com/gestion/stock/security/SecurityConfig.java`

**Add Keycloak resource server support:**

```java
package com.gestion.stock.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final KeycloakJwtAuthenticationConverter keycloakJwtAuthenticationConverter;  // ← NEW

    @Value("${security.keycloak.enabled:false}")  // ← NEW
    private boolean keycloakEnabled;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/gestionStock/api/v1/auth/**").permitAll()  // ← Fixed path
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                );

        // ↓ NEW: Add Keycloak support if enabled
        if (keycloakEnabled) {
            httpSecurity.oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtAuthenticationConverter))
            );
        }
        // ↑ NEW

        // Legacy JWT filter (always active)
        httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

**What changed:**

- Inject `KeycloakJwtAuthenticationConverter`
- Read `security.keycloak.enabled` property
- If enabled, configure OAuth2 resource server
- Fixed path matcher to match your context path

---

## Step 5.2: Enable Keycloak

**File:** `src/main/resources/application.properties`

**Change this line:**

```properties
security.keycloak.enabled=true
```

---

## ✅ TEST PHASE 5 (Final Integration Test)

### 5.1: Test Legacy Login Still Works

```bash
# Start app
mvn spring-boot:run

# Login with existing user
curl -X POST http://localhost:8080/gestionStock/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"yourpass"}'

# Should return: {"token":"eyJhbGc...", ...}

# Use token to access protected endpoint
curl -X GET http://localhost:8080/gestionStock/api/v1/produits \
  -H "Authorization: Bearer <your-legacy-token>"

# Should work as before
```

### 5.2: Setup Keycloak (if not done)

**Option A: Docker (recommended for beginners)**

```bash
docker run -d \
  -p 8180:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  --name keycloak \
  quay.io/keycloak/keycloak:latest start-dev
```

**Option B: Download standalone**

- Download from https://www.keycloak.org/downloads
- Extract and run: `bin/kc.sh start-dev` (Linux/Mac) or `bin\kc.bat start-dev` (Windows)

**Access Keycloak:**

- Open: http://localhost:8180
- Login: admin / admin
- Create realm: `gestion-stock`
- Create client: `gestion-stock-api`
  - Client type: OpenID Connect
  - Client ID: `gestion-stock-api`
  - Valid redirect URIs: `*` (for testing)
  - Access Type: bearer-only (or confidential)
- Create user: testuser
  - Username: testuser
  - Email: testuser@example.com
  - Set password (disable temporary)

### 5.3: Get Keycloak Token (Postman)

**Postman Setup:**

1. Create new request
2. Authorization tab → Type: OAuth 2.0
3. Configure:
   - Grant Type: Password Credentials
   - Access Token URL: `http://localhost:8180/realms/gestion-stock/protocol/openid-connect/token`
   - Client ID: `gestion-stock-api`
   - Username: testuser
   - Password: <your-password>
4. Click "Get New Access Token"
5. Copy the access token

### 5.4: Test Keycloak Token

```bash
# Call your API with Keycloak token
curl -X GET http://localhost:8080/gestionStock/api/v1/produits \
  -H "Authorization: Bearer <keycloak-access-token>"

# First time: creates user automatically
# Check logs:
# "First-time Keycloak user, provisioning: testuser"
# "Created new Keycloak user: id=X, username=testuser"

# Check database
mysql -u root -p097680 -e "SELECT id, username, auth_provider, external_subject FROM gestion_stock_db.users WHERE auth_provider='KEYCLOAK';"

# Should show new user with KEYCLOAK provider
```

### 5.5: Assign Role to Keycloak User

Since new users have `role=null`, they can't access protected endpoints yet:

```bash
# Check user permissions endpoint
curl -X GET http://localhost:8080/gestionStock/api/v1/user-management/users \
  -H "Authorization: Bearer <admin-legacy-token>"

# Find the new Keycloak user ID

# Assign role via your user management endpoint
curl -X POST http://localhost:8080/gestionStock/api/v1/user-management/assign-role \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{"userId": <keycloak-user-id>, "roleName": "USER"}'

# Now retry accessing protected endpoint with Keycloak token
# Should work!
```

✅ **Commit:** `git commit -m "feat: enable dual authentication (legacy JWT + Keycloak)"`

---

# 🎉 Success Checklist

- [ ] Legacy `/auth/login` works
- [ ] Legacy JWT tokens authenticate successfully
- [ ] Keycloak tokens authenticate successfully
- [ ] First Keycloak login creates local user automatically
- [ ] Both auth methods load permissions from same DB
- [ ] Role assignment works for Keycloak users
- [ ] No errors in logs

---

# 🐛 Troubleshooting

## Problem: "Issuer mismatch" error

**Cause:** `spring.security.oauth2.resourceserver.jwt.issuer-uri` doesn't match token's `iss` claim

**Fix:**

```bash
# Decode your Keycloak token at jwt.io
# Check the "iss" claim value
# Update application.properties to match EXACTLY
```

## Problem: Legacy login broken after adding Keycloak

**Cause:** TokenClassifier treating legacy tokens as Keycloak

**Fix:**

- Ensure your legacy JWT has NO `iss` claim, or
- Update TokenClassifier to check `iss` matches exactly

## Problem: "User has no role" even after assignment

**Cause:** Permission cache or CustomUserDetailsService not refreshing

**Fix:**

```bash
# Restart app
# Or get a new Keycloak token (tokens have permissions at time of issuance)
```

## Problem: "Could not resolve parameter" for KeycloakJwtAuthenticationConverter

**Cause:** Circular dependency or missing @Component

**Fix:**

- Ensure KeycloakJwtAuthenticationConverter has `@Component`
- Ensure CustomUserDetailsService is @Service

---

# 🚀 Next Steps

## Production Hardening

1. **Externalize JWT secret** (JwtUtil uses random key per restart)

```properties
jwt.secret=your-256-bit-secret-key-here
```

2. **Use environment variables for Keycloak**

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=${KEYCLOAK_ISSUER_URI}
```

3. **Add audience validation**

```java
// In KeycloakJwtAuthenticationConverter
if (!jwt.getAudience().contains("gestion-stock-api")) {
    throw new JwtException("Invalid audience");
}
```

4. **Implement user sync** (optional)

- Sync Keycloak roles to DB roles
- Update user profile on each login

## Migration Strategy

1. **Week 1-2:** Run dual auth, monitor logs
2. **Week 3-4:** Migrate clients to Keycloak
3. **Week 5:** Deprecate legacy `/auth/login`
4. **Week 6+:** Remove JwtAuthenticationFilter

---

# 📚 References

- [Spring Security OAuth2 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html)
- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [JWT.io](https://jwt.io) - Decode tokens for debugging

---

**Questions?** Check logs with `logging.level.com.gestion.stock.security=DEBUG`
