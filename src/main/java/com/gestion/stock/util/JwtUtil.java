package com.gestion.stock.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {


    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @Value("${jwt.expiration:86400000}") // 24 hours default
    private long jwtExpiration;


    public String generateToken(String username){
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key)
                .compact();

    }

    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    public String extractUsername(String token){
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public boolean isTokenExpired(String token){
        Claims claims = extractAllClaims(token);
        return claims.getExpiration().before(new Date());
    }
    public boolean isTokenValid(String token,String username){
        return username.equals(extractUsername(token)) && !isTokenExpired(token);
    }

}
