# Quick Start Guide - OAuth2 Configuration

## ✅ Configuration Added Successfully!

The missing OAuth2 configuration has been added to your `application.properties`.

---

## 🚀 TO START YOUR APPLICATION NOW:

### Option 1: Start with Keycloak (Full OAuth2 Support)

**Prerequisites:**
1. Keycloak must be running on `http://localhost:8180`
2. Realm `oauth2-sup` must exist
3. Update these values in `application.properties`:
   ```properties
   spring.security.oauth2.client.registration.keycloak.client-id=YOUR_CLIENT_ID
   spring.security.oauth2.client.registration.keycloak.client-secret=YOUR_CLIENT_SECRET
   ```

**Then run:**
```bash
mvn spring-boot:run
```

---

### Option 2: Start WITHOUT Keycloak (Local JWT Only)

If Keycloak is not ready yet, you can temporarily disable OAuth2:

#### Step 1: Comment out OAuth2 configuration in `application.properties`
Add `#` before each OAuth2 line:
```properties
# spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8180/realms/oauth2-sup
# spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8180/realms/oauth2-sup/protocol/openid-connect/certs
# ... (comment all OAuth2 lines)
```

#### Step 2: Modify `SecurityConfig.java`
Remove or comment out the OAuth2 resource server configuration:

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/auth/**").permitAll()
                    .anyRequest().authenticated()
            )
            // COMMENT OUT THIS SECTION:
            // .oauth2ResourceServer(oauth2 -> oauth2
            //         .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtAuthenticationConverter))
            //         .authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exception -> exception
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                    .accessDeniedHandler(customAccessDeniedHandler)
            )
            .build();
}

// ALSO COMMENT OUT OR REMOVE THIS:
// @Bean
// public JwtDecoder jwtDecoder() {
//     return JwtDecoders.fromIssuerLocation(issuerUri);
// }
```

#### Step 3: Run the application
```bash
mvn spring-boot:run
```

Now you can use **only local JWT authentication** via `/auth/login`

---

## 🧪 Test Your Authentication

### Test Local JWT Authentication (Works without Keycloak):

**1. Login:**
```bash
POST http://localhost:8080/gestionStock/api/v1/auth/login
Content-Type: application/json

{
  "username": "your_username",
  "password": "your_password"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**2. Use token in requests:**
```bash
GET http://localhost:8080/gestionStock/api/v1/some-endpoint
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

### Test OAuth2/Keycloak Authentication (Requires Keycloak):

**1. Get token from Keycloak:**
```bash
POST http://localhost:8180/realms/oauth2-sup/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=password&
client_id=YOUR_CLIENT_ID&
client_secret=YOUR_CLIENT_SECRET&
username=keycloak_user&
password=keycloak_password
```

**2. Use Keycloak token:**
```bash
GET http://localhost:8080/gestionStock/api/v1/some-endpoint
Authorization: Bearer KEYCLOAK_ACCESS_TOKEN
```

---

## 📝 Current Configuration Summary

### What Works NOW:
- ✅ Application can start (no more "Issuer URI not found" error)
- ✅ Local JWT authentication via `/auth/login`
- ✅ Database migrations are correct
- ✅ All dependencies are present

### What Needs Keycloak Running:
- ❌ OAuth2/Keycloak token validation
- ❌ Auto-creation of Keycloak users in database
- ❌ OAuth2 login flow

### Files Modified:
- ✅ `src/main/resources/application.properties` (OAuth2 config added)

---

## 🔧 Troubleshooting

### Error: "Connection refused: connect"
**Cause:** Keycloak is not running or wrong URL  
**Solution:** Start Keycloak or disable OAuth2 (see Option 2 above)

### Error: "Unable to resolve Configuration"
**Cause:** OAuth2 issuer-uri is configured but Keycloak is not accessible  
**Solution:** Comment out OAuth2 config or start Keycloak

### Error: "client_id not found"
**Cause:** Client not configured in Keycloak  
**Solution:** Create client in Keycloak admin console

---

## 📚 Next Steps

1. **If using Keycloak:** Follow setup guides in `/docs` folder
2. **If testing locally:** Use Option 2 above to disable OAuth2 temporarily
3. **Update client credentials** in `application.properties` when ready

---

## ⚠️ Important Notes

- Your app supports **DUAL authentication**: Local JWT + Keycloak OAuth2
- Users can exist with either `auth_provider=LOCAL` or `auth_provider=KEYCLOAK`
- The database migration properly handles both authentication types
- `password` column is nullable to support OAuth2 users

---

For detailed analysis, see: **OAUTH_CONFIGURATION_ANALYSIS.md**

