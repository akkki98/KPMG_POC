package com.example.demo.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;

@Service
@Slf4j
public class JwtService {

    @Value("${security.jwt.secret-key:changemechangemechangemechangeme1234567890}")
    private String secretKey; // Base64 encoded recommended.

    @Value("${security.jwt.expiration-seconds:3600}")
    private long expirationSeconds;

    private SecretKey getSigningKey() {
        byte[] keyBytes;
        boolean base64 = true;
        try {
            keyBytes = Decoders.BASE64.decode(secretKey);
        } catch (RuntimeException ex) { // covers DecodingException & IllegalArgumentException
            base64 = false;
            keyBytes = secretKey.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        }
        if (keyBytes.length < 32) { // 256 bits recommended
            throw new IllegalStateException("JWT secret too short (<32 bytes). Provide a 256-bit (32+ byte) secret (Base64-encoded preferred).");
        }
        if (log.isDebugEnabled()) {
            log.debug("Using {} JWT secret ({} bytes)", base64 ? "Base64-decoded" : "raw", keyBytes.length);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        try {
            return Jwts.builder()
                .setClaims(new HashMap<>(claims))
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
        } catch (RuntimeException e) {
            log.error("Failed to generate JWT token", e);
            throw e;
        }
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public String extractSubject(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}

