package com.securityagent.api_security_agent.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // secret key used to sign tokens
    // must be at least 32 characters
    private static final String SECRET =
            "mySuperSecretKeyForJWTSigning12345";

    // token expires after 24 hours
    private static final long EXPIRATION =
            24 * 60 * 60 * 1000;

    // generate signing key from secret
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // generates JWT token for a username
    public String generateToken(String username) {
        return Jwts.builder()
                // set username as subject
                .setSubject(username)
                // set issue time
                .setIssuedAt(new Date())
                // set expiry time
                .setExpiration(new Date(
                        System.currentTimeMillis() + EXPIRATION))
                // sign with secret key
                .signWith(getSigningKey(),
                        SignatureAlgorithm.HS256)
                .compact();
    }

    // extracts username from token
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // checks if token is valid and not expired
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            System.out.println("Invalid JWT: " + e.getMessage());
            return false;
        }
    }

}