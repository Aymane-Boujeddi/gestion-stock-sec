# Keycloak + Spring Security (Advanced Step‑by‑Step) — Your Project

This document is tailored to your project structure and current configuration:

- Spring Boot `3.5.7`, Java `17`
- Context path: `/gestionStock/api/v1` (from `server.servlet.context-path`)
- Existing legacy auth: `JwtAuthenticationFilter` + `JwtUtil` (JJWT)
- DB authorization model: `RoleApp` permissions + per-user overrides (via `CustomUserDetailsService`)
- User entity already supports OAuth users:
  - `password` is nullable
  - `authProvider` (non-null)
  - `clientIdSub` unique (Keycloak `sub`)

Goal: Add Keycloak OAuth2 **Resource Server** authentication **on top of** your existing legacy JWT filter, while keeping **all permissions** enforced from your database.

---

## Step 0 — Understand the “2 token worlds” (advanced concept: issuer & trust boundaries)

You are mixing two different token ecosystems:

### A) Legacy JWT (your own app)

- You **issue** and **verify** the token yourself.
- Verification depends on your signing secret/key.
- In your project, `JwtUtil` uses JJWT and extracts `subject` as `username`.

### B) Keycloak access token (OAuth2 / OIDC)

- Keycloak **issues** the token.
- Your API is a **Resource Server**: it only verifies access tokens and enforces authorization.
- Verification depends on Keycloak’s **public keys** exposed through **JWKS**.

**Advanced idea: trust boundary**

- With Keycloak tokens, you trust Keycloak to authenticate the user.
- You still **do not** trust Keycloak for your app’s permissions if your permissions live in your DB. You only use Keycloak to identify the user (`sub`).

---

## Step 1 — Fix a critical path-matcher mismatch (advanced concept: context path vs request matchers)

Your app runs under:

- Base path: `/gestionStock/api/v1`

But your Spring Security config currently permits:

- `/auth/**`

While your filter’s `shouldNotFilter` checks:

- `/gestionStock/api/v1/auth/`

**Why this matters (advanced concept: security matcher evaluation):**
Spring Security evaluates `authorizeHttpRequests()` based on the request path (including your context path). If your matcher doesn’t include the context path, you can end up protecting endpoints you expected to be public.

**Rule of thumb:** Always write matchers that match the real runtime URL.

Expected public endpoints (based on your controllers and filter exclusion):

- `/gestionStock/api/v1/auth/**`

### Code snippet — Fix matchers in your `SecurityConfig`

In your project, your controller is `@RequestMapping("/auth")` and your app has context path `/gestionStock/api/v1`, so the real runtime URL is `/gestionStock/api/v1/auth/**`.

Update your permit rule accordingly (snippet shown later in Step 3).

---

## Step 2 — Add Keycloak “issuer” config (advanced concept: OIDC discovery & JWKS)

For OAuth2 Resource Server, Spring needs to know **where** the realm is.

Add to `application.properties`:

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://<KEYCLOAK_HOST>:<KEYCLOAK_PORT>/realms/<REALM>
```

### Code snippet — What to add in `src/main/resources/application.properties`

Add the issuer property below your existing settings:

```properties
# Keycloak OAuth2 Resource Server (API validates Keycloak access tokens)
# Example (local Keycloak): http://localhost:8081/realms/gestion-stock
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://<KEYCLOAK_HOST>:<KEYCLOAK_PORT>/realms/<REALM>
```

Notes:

- If your Spring Boot app runs in Docker and Keycloak runs in another container, `<KEYCLOAK_HOST>` is usually the docker service name (not `localhost`).
- The value must be the exact realm issuer URL (no trailing slashes preferred).

**Optional but recommended:** Add explicit Jackson dependency to `pom.xml` (Jackson is used for token inspection in the filter):

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

Spring Boot parent will manage the version automatically.

### What Spring does with `issuer-uri` internally

When you set `issuer-uri`, Spring Security uses OIDC discovery:

- Fetches `/.well-known/openid-configuration` from the realm
- Reads:
  - `issuer`
  - `jwks_uri`
  - supported algorithms, etc.
- Builds a `JwtDecoder` (usually `NimbusJwtDecoder`) that:
  - downloads Keycloak public keys from `jwks_uri`
  - caches keys
  - verifies signatures and standard claims

**Advanced concept: JWKS key rotation**
Keycloak can rotate signing keys. If you hardcode a single key, verification breaks after rotation. Using `issuer-uri` + JWKS means your resource server will keep working across rotations.

---

## Step 3 — Enable Resource Server in the filter chain (advanced concept: how Spring inserts filters)

You will add:

- `.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> ...))`

### What that actually wires up (high-level)

This adds (conceptually):

- `BearerTokenAuthenticationFilter`
- A `JwtAuthenticationProvider`
- A `JwtDecoder` (Nimbus)

### Request-time flow (advanced, precise)

On every request:

1. `BearerTokenAuthenticationFilter` runs.
2. It looks for `Authorization: Bearer <token>`.
3. It calls the `AuthenticationManager`.
4. `JwtAuthenticationProvider` tries to decode/validate the token:
   - signature verified using Keycloak’s JWKS
   - checks `exp` / time validity
   - checks `iss` matches configured issuer
5. If valid, Spring builds a `Jwt` object (claims are now trusted).
6. Then Spring calls your **JwtAuthenticationConverter**:
   - converts `Jwt` into an `Authentication` (principal + authorities)
7. It stores the `Authentication` into `SecurityContextHolder`.
8. Authorization happens later via:
   - `authorizeHttpRequests()`
   - `@PreAuthorize`, etc.

**Important advanced takeaway:**
Your converter runs **after** cryptographic validation. It is not responsible for verifying authenticity—only for mapping.

### Code snippet — Update `src/main/java/com/gestion/stock/security/SecurityConfig.java`

This snippet does 3 things:

- Fixes the `permitAll()` matcher to include your context path.
- Enables Keycloak Resource Server validation.
- Keeps your legacy JWT filter.

```java
package com.gestion.stock.security;


import lombok.RequiredArgsConstructor;
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
  private final KeycloakJwtAuthenticationConverter keycloakJwtAuthenticationConverter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            // IMPORTANT: your app has context-path=/gestionStock/api/v1
            .requestMatchers("/gestionStock/api/v1/auth/**").permitAll()
            .anyRequest().authenticated()
        )
        // Keycloak bearer token authentication
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtAuthenticationConverter))
        )
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(customAccessDeniedHandler)
        )
        // Legacy JWT authentication (your own tokens)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
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

Important:

- With this setup, BOTH Keycloak tokens and legacy tokens will arrive as `Authorization: Bearer ...`.
- Your legacy filter must not try to parse Keycloak tokens (Step 5 has the snippet).

---

## Step 4 — Understand `jwtAuthenticationConverter` (advanced concept: mapping identity to your DB model)

Your project’s permission system is DB-driven:

- Roles -> permissions
- Optional per-user overrides

So Keycloak authentication must still end with:

- a user identity that your DB understands
- authorities that represent DB permissions (`Permission.name`)

### The “correct” stable identifier from Keycloak: `sub`

- `sub` is the stable user identifier inside the realm.
- Access tokens change every login, but `sub` stays constant.

### Mapping strategy

Use:

- `authProvider = "KEYCLOAK"` (or similar)
- `clientIdSub = <jwt.sub>`

Then you can:

- Find or create the user in your DB.
- Keep `role = null` initially (like your local register flow) so the user is “pending activation”.
- Enforce permissions only after admin assigns a role.

**Advanced concept: why not use email as key**

- Email can change.
- Users can share emails across realms.
- Some tokens may not include email.
- `sub` is designed for this identity-linking purpose.

**IMPORTANT:** You must also add a new repository method (see below) to avoid lazy-loading exceptions.### Code snippet — Add a converter class (JIT provisioning + DB permissions)

Create this file:

- `src/main/java/com/gestion/stock/security/KeycloakJwtAuthenticationConverter.java`

This converter:

- Uses Keycloak `sub` to find/create a local DB user.
- Sets `authProvider="KEYCLOAK"` and `clientIdSub=sub`.
- Keeps `role=null` for “pending activation” (so user gets 403 until admin assigns a role).
- Produces authorities from your DB permission model (role permissions + per-user overrides).

```java
package com.gestion.stock.security;

import com.gestion.stock.entity.Permission;
import com.gestion.stock.entity.User;
import com.gestion.stock.entity.UserPermission;
import com.gestion.stock.repository.RoleAppRepository;
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
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

  private static final String PROVIDER = "KEYCLOAK";

  private final UserRepository userRepository;
  private final RoleAppRepository roleAppRepository;

  @Override
  public AbstractAuthenticationToken convert(Jwt jwt) {
    String sub = jwt.getSubject();

    // Use Keycloak sub as base for unique username (simpler & collision-free)
    String usernameBase = "kc_" + sub.substring(0, Math.min(8, sub.length()));

    User user = userRepository.findByAuthProviderAndClientIdSubWithPermissions(PROVIDER, sub)
        .orElseGet(() -> createKeycloakUser(sub, usernameBase));

    List<GrantedAuthority> authorities = buildAuthoritiesFromDb(user);
    CustomUserDetails principal = new CustomUserDetails(user, authorities);

    return new UsernamePasswordAuthenticationToken(principal, null, authorities);
  }

  private User createKeycloakUser(String sub, String preferredUsername) {
    String uniqueUsername = makeUsernameUnique(preferredUsername);

    User user = User.builder()
        .username(uniqueUsername)
        .password(null)usernameBase) {
    // clientIdSub is unique, so this guarantees username uniqueness
    String username = userRepository.existsByUsername(usernameBase)
        ? usernameBase + "_" + System.currentTimeMillis()
        : usernameBase;

    User user = User.builder()
        .username(username)
        .password(null)
        .authProvider(PROVIDER)
        .clientIdSub(sub)
        .role(null) // pending activation: no permissions until admin assigns role
        .build();

    return userRepository.save(user)();
    }

    List<Permission> rolePermissions = roleAppRepository.findPermissionsByRole(user.getRole().getName());
    List<Permission> effective = applyUserPermissionOverrides(rolePermissions, user.getUserPermissions());

    return effective.stream()
        .map(p -> new SimpleGrantedAuthority(p.getName()))
        .collect(Collectors.toList());
  }

  private List<Permission> applyUserPermissionOverrides(List<Permission> rolePermissions, List<UserPermission> userPermissions) {
    if (userPermissions == null || userPermissions.isEmpty()) {
      return rolePermissions;
    }

    Set<String> permissionNames = rolePermissions.stream()
        .map(Permission::getName)
        .collect(Collectors.toSet());

    for (UserPermission up : userPermissions) {
      if (up.getPermission() == null) continue;
      String permName = up.getPermission().getName();

      if (up.isGranted()) {
        permissionNames.add(permName);
      } else {
        permissionNames.remove(permName);
      }
    }

    Set<Permission> allPermissions = new HashSet<>(rolePermissions);
    userPermissions.stream()
        .map(UserPermission::getPermission)
        .filter(p -> p != null)
        .forEach(allPermissions::add);

    return allPermissions.stream()
        .filter(p -> permissionNames.contains(p.getName()))
        .collect(Collectors.toList());
  }

  private static String firstNonBlank(String... values) {
    for (String v : values) {
      if (v != null && !v.isBlank()) return v;
    }
    return null;
  }
}
```

If you want Keycloak users to stay “invisible” until approved:

- keep `role=null` as shown
- your authorization will naturally return `403` due to zero authorities
  }

````

**REQUIRED:** Add this method to `UserRepository.java` to avoid lazy-loading exceptions:

```java
@Query("SELECT DISTINCT u FROM User u " +
       "LEFT JOIN FETCH u.role r " +
       "LEFT JOIN FETCH u.userPermissions up " +
       "LEFT JOIN FETCH up.permission " +
       "WHERE u.authProvider = :authProvider AND u.clientIdSub = :clientIdSub")
Optional<User> findByAuthProviderAndClientIdSubWithPermissions(
    @Param("authProvider") String authProvider,
    @Param("clientIdSub") String clientIdSub
);
````

Why this is needed:

- The converter runs outside a transaction context (in the filter chain)
- Without `JOIN FETCH`, accessing `user.getUserPermissions()` throws `LazyInitializationException`
- This mirrors the pattern already used in `findUserByUsername`JwtAuthenticationFilter`that always tries to parse *any* Bearer token with your`JwtUtil`.

If you enable Keycloak resource server, the same request will have a Bearer token that might be:

- Keycloak JWT (RS256)
- Your legacy JWT (HS256)

### Why this is dangerous

- Your legacy filter will attempt to parse Keycloak tokens.
- That can cause exceptions and clear the context.

### Two safe patterns

#### Pattern A (recommended for dual-auth): Skip Keycloak tokens in legacy filter

- Detect token “type” by reading untrusted header/payload (no signature check) only to decide routing.
- Example: read `iss` claim.

If `iss` matches Keycloak realm issuer → do not process in legacy filter.

**Advanced concept: why reading claims before verification is still OK here**
You are not trusting the content, only using it to choose which verifier should handle it.

#### Pattern B: Use separate SecurityFilterChain per path

- One chain for `/auth/**` and legacy-only endpoints
- Another chain for all other endpoints with resource server

This is more complex but gives clearer boundaries.

### Code snippet — Update your legacy filter so it skips Keycloak tokens

Edit:

- `src/main/java/com/gestion/stock/security/JwtAuthenticationFilter.java`

Goal: if the Bearer token is a Keycloak token, do NOT try to parse it with `JwtUtil`.

This snippet decodes the JWT payload (base64url) to read `iss` and compares it to your configured issuer.

```java
package com.gestion.stock.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.stock.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final CustomUserDetailsService userDetailsService;

  @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:}")
  private String keycloakIssuer;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String header = request.getHeader("Authorization");

    // If it looks like a Keycloak token, let Spring Resource Server handle it.
    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);
      if (isKeycloakToken(token)) {
        filterChain.doFilter(request, response);
        return;
      }
    }

    String token = null;
    String username = null;

    try {
      if (header != null && header.startsWith("Bearer ")) {
        token = header.substring(7);
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

  private boolean isKeycloakToken(String token) {
    if (keycloakIssuer == null || keycloakIssuer.isBlank()) {
      return false;
    }

    // JWT must have 3 parts
    String[] parts = token.split("\\.");
    if (parts.length != 3) {
      return false;
    }

    try {
      String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
      JsonNode payload = objectMapper.readTree(payloadJson);
      String iss = payload.path("iss").asText(null);
      return keycloakIssuer.equals(iss);
    } catch (Exception e) {
      return false;
    }
  }
}
```

Why this works:

- You do NOT trust the payload for authentication.
- You only use `iss` to route the token to the correct verifier.
- The real validation is still done by Spring’s `JwtDecoder` for Keycloak tokens.
  **Note:** `ObjectMapper` is not `final` because `@RequiredArgsConstructor` only manages constructor injection for final fields. Since we initialize it inline, it must be non-final to avoid compilation errors.

---

## Step 6 — Add stronger token validation (advanced concept: issuer vs audience)

Spring validates `iss` (issuer) automatically when using `issuer-uri`.

But you may also want to validate:

- `aud` (audience)

### Why audience matters

A token may be issued by the same realm but intended for a different API/client. Audience validation helps ensure the token is meant for _your_ resource server.

Typical approaches:

- Configure Keycloak client so the access token includes proper audience.
- Add an audience validator to the `JwtDecoder`.

(If you keep things minimal at first, you can postpone this until after the first working integration.)

---

## Step 7 — Understand statelessness, CSRF, and CORS (advanced concept: API security properties)

Your config uses:

- `SessionCreationPolicy.STATELESS`
- `csrf.disable()`

### Why this is correct for bearer tokens

- Stateless means the server does not store login sessions.
- Every request must carry its own authentication (`Authorization: Bearer ...`).

### CSRF nuance

- CSRF protection is mainly for cookie-based browser sessions.
- If you use Authorization headers (bearer token), CSRF is typically disabled.

### CORS nuance

- If you have a frontend on a different origin, you must configure CORS properly.
- CORS is not authentication, but without it browsers will block your requests.

---

## Step 8 — Keep authorization unified (advanced concept: authority model consistency)

Your system produces authorities like:

- `new SimpleGrantedAuthority(permission.getName())`

This is good because:

- Both legacy JWT and Keycloak auth can end up with identical authorities.
- Your controllers/services can use the same permission checks.

### Recommended practice

Prefer permissions like:

- `STOCK_READ`, `STOCK_WRITE`, etc.

Then use either:

- `@PreAuthorize("hasAuthority('STOCK_READ')")`

or `authorizeHttpRequests()` rules.

---

## Step 9 — Testing workflow (advanced concept: verify each layer independently)

### 1) Verify Keycloak token validation

- Obtain an access token from Keycloak.
- Call a protected endpoint:
  - Without token → should be 401
  - With token but no local role assigned → should be 403 (if you map zero authorities)

### 2) Verify JIT provisioning (DB user created)

- After first request with Keycloak token:
  - a row should be inserted in `users`
  - `client_id_sub` should be filled
  - `auth_provider` should be set
  - `password` should be null

### 3) Verify role assignment & permission enforcement

- Assign a role to the created user in DB
- Retry endpoint protected by permissions

---

## Step 10 — Common pitfalls in your current project

1. **Context path mismatch**

- Security matchers should include `/gestionStock/api/v1/...`

2. **Legacy JWT key lifecycle**

- If your `JwtUtil` uses an in-memory generated key, tokens die after restart.
- That’s fine for a demo, but unstable for production.

3. **Swallowing exceptions in the legacy filter**

- Your filter clears the context on any exception.
- If it tries to parse a Keycloak token, it may clear an auth set by the resource server.
- That’s why you need token discrimination or chain separation.

---

## Step 11 — Minimal target end-state (what you should have when done)

- `application.properties` has `spring.security.oauth2.resourceserver.jwt.issuer-uri=...`
- `SecurityConfig`:
  - permits `/gestionStock/api/v1/auth/**`
  - enables `.oauth2ResourceServer(...jwtAuthenticationConverter(...))`
  - keeps stateless settings
- A converter that:
  - reads `sub`
  - finds/creates local `User` using (`authProvider`, `clientIdSub`)
  - loads permissions from DB via your existing logic
- Legacy JWT filter updated to not break Keycloak flow

---

## Appendix — Glossary (advanced concepts)

- **Resource Server**: API that receives bearer access tokens and validates them.
- **Issuer (`iss`)**: Who created the token. Used to prevent accepting tokens from unknown sources.
- **JWKS**: JSON Web Key Set. Public keys endpoint used to verify signatures.
- **JwtDecoder**: Component that verifies JWT signature and standard claims.
- **AuthenticationConverter**: Maps a verified `Jwt` into Spring Security `Authentication` (principal + authorities).
- **Authorities**: Permissions/roles used by Spring Security authorization.
