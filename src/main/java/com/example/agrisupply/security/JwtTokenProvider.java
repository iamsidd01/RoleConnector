package com.example.agrisupply.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${app.jwtSecret}")
    private String jwtSecretString; // Store the secret as a string initially

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    private SecretKey jwtSecretKey; // Use SecretKey for type safety

    @PostConstruct
    public void init() {
        // Decode the Base64 encoded secret string and create a SecretKey
        try {
            // Ensure the key size is appropriate for the algorithm (HS256 needs >= 256 bits)
             byte[] keyBytes = java.util.Base64.getDecoder().decode(jwtSecretString);
             if (keyBytes.length < 32) { // 32 bytes = 256 bits
                 log.error("JWT Secret key size is insufficient for HS256. Expected at least 256 bits (32 bytes). Found {} bytes.", keyBytes.length);
                 // You might want to throw an exception here or generate a secure key
                 // For development, generating one:
                 // this.jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
                 // log.warn("Generated temporary secure JWT key for development.");
                  throw new IllegalArgumentException("JWT Secret key size is insufficient. Please provide a Base64 encoded key of at least 256 bits.");

             }
             this.jwtSecretKey = Keys.hmacShaKeyFor(keyBytes);
             log.info("JWT Secret Key initialized successfully.");
        } catch (IllegalArgumentException e) {
            log.error("Invalid Base64 encoding for JWT secret: {}", e.getMessage());
            throw e; // Re-throw to prevent application startup with invalid config
        }
    }


    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

         // Extract roles/authorities
        String roles = authentication.getAuthorities().stream()
               .map(GrantedAuthority::getAuthority)
               .collect(Collectors.joining(","));


        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                 .claim("roles", roles) // Add roles as a claim
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecretKey, SignatureAlgorithm.HS256) // Specify the algorithm
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecretKey).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }
}
