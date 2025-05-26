package com.rudenko.socialmedia.service.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import javax.crypto.SecretKey

@Service
class JwtService {

    private final SecretKey secretKey

    JwtService(@Value('${jwt.secret}') String secretKey) {
        byte[] decodedKey = secretKey.getBytes()

        if (decodedKey.length < 32) {
            throw new IllegalArgumentException("JWT Secret key must be at least 256 bits (32 bytes).")
        }

        this.secretKey = Keys.hmacShaKeyFor(decodedKey)
    }

    String generateToken(String username) {
        return Jwts.builder()
                   .setClaims(Map.of("username", username))
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                   .signWith(secretKey)
                   .compact()
    }

    String extractUsername(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(secretKey)
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .get("username", String.class)
    }

    boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)

            return true
        } catch (Exception parserException) {
            //ignoring error, just return false (it is not the right approach)

            return false
        }
    }


}
