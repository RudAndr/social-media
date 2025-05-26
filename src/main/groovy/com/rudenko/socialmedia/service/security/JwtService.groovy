package com.rudenko.socialmedia.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Service

@Service
class JwtService {

    private static final String SECRET_KEY = "your_secret_key"; // Замінити на реальне секретне значення.

    String generateToken(String username) {
        return Jwts.builder()
                .setClaims(Map.of("username", username))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 годин
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .get("username", String.class);
    }

    boolean isTokenValid(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
