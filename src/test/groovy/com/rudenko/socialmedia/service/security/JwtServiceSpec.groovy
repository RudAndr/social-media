package com.rudenko.socialmedia.service.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import spock.lang.Specification
import spock.lang.Subject

import javax.crypto.SecretKey

class JwtServiceSpec extends Specification {

    // Using a fixed test secret key that is 32 bytes (256 bits) long
    static final String TEST_SECRET_KEY = "this_is_a_test_secret_key_for_jwt_testing_123456"

    @Subject
    JwtService jwtService

    def setup() {
        jwtService = new JwtService(TEST_SECRET_KEY)
    }

    def "should generate token with username in claims"() {
        given: "we have a username for token generation"
        def username = "testuser"

        when: "generate token method is called"
        def token = jwtService.generateToken(username)

        then: "we expect a valid token with correct claims"
        token != null
        token.length() > 0

        // Verify token contents
        def claims = parseToken(token)
        claims.get("username") == username
        claims.getExpiration() > new Date()
        claims.getIssuedAt() <= new Date()
    }

    def "should extract username from token"() {
        given: "we have a token with a username"
        def username = "testuser"
        def token = jwtService.generateToken(username)

        when: "extract username method is called"
        def extractedUsername = jwtService.extractUsername(token)

        then: "we expect the extracted username to match the original"
        extractedUsername == username
    }

    def "should validate valid token"() {
        given: "we have a valid token"
        def username = "testuser"
        def token = jwtService.generateToken(username)

        when: "isTokenValid method is called"
        def isValid = jwtService.isTokenValid(token)

        then: "we expect the token to be valid"
        isValid
    }

    def "should invalidate tampered token"() {
        given: "we have a token that has been tampered with"
        def username = "testuser"
        def token = jwtService.generateToken(username)
        def tamperedToken = token.substring(0, token.length() - 5) + "12345" // Change the last 5 characters

        when: "isTokenValid method is called with the tampered token"
        def isValid = jwtService.isTokenValid(tamperedToken)

        then: "we expect the token to be invalid"
        !isValid
    }

    def "should invalidate expired token"() {
        given: "we have an expired token"
        // Create a service that generates tokens that expire immediately
        def expiredJwtService = Spy(JwtService, constructorArgs: [TEST_SECRET_KEY]) {
            generateToken(_ as String) >> {
                String username ->
                    Jwts.builder()
                        .setClaims(Map.of("username", username))
                        .setIssuedAt(new Date(System.currentTimeMillis() - 2000)) // 2 seconds ago
                        .setExpiration(new Date(System.currentTimeMillis() - 1000)) // 1 second ago (already expired)
                        .signWith(Keys.hmacShaKeyFor(TEST_SECRET_KEY.getBytes()))
                        .compact()
            }
        }

        def token = expiredJwtService.generateToken("testuser")

        when: "isTokenValid method is called with the expired token"
        def isValid = jwtService.isTokenValid(token)

        then: "we expect the token to be invalid"
        !isValid
    }

    def "should throw exception for secret key less than 32 bytes"() {
        given: "we have a secret key that is too short"
        def shortSecretKey = "too_short"

        when: "JwtService constructor is called with the short key"
        new JwtService(shortSecretKey)

        then: "we expect an IllegalArgumentException to be thrown"
        thrown(IllegalArgumentException)
    }

    // Helper method to parse a token for verification
    private def parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(TEST_SECRET_KEY.getBytes())
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
    }
}
