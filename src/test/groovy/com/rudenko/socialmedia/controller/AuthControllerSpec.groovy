package com.rudenko.socialmedia.controller

import com.rudenko.socialmedia.controller.advice.GlobalControllerAdvice
import com.rudenko.socialmedia.data.entity.User
import com.rudenko.socialmedia.data.request.user.UserCreateRequest
import com.rudenko.socialmedia.service.security.JwtService
import com.rudenko.socialmedia.service.user.UserInfoService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import spock.lang.Specification

class AuthControllerSpec extends Specification {
    private WebTestClient webTestClient
    private JwtService jwtService
    private UserInfoService userInfoService
    private PasswordEncoder passwordEncoder

    void setup() {
        jwtService = Mock(JwtService)
        passwordEncoder = Mock(BCryptPasswordEncoder)
        userInfoService = Mock(UserInfoService)

        webTestClient = WebTestClient
                .bindToController(new AuthController(userInfoService, passwordEncoder, jwtService))
                .controllerAdvice(new GlobalControllerAdvice())
                .build()
    }

    def "should authenticate user successfully"() {
        given: "we have valid user credentials"
            def username = "testUser"
            def password = "password"
            def token = "test-token"
            def user = new User(username: username, password: password)

        when: "login endpoint is called with valid credentials"
            def result = webTestClient
                    .post()
                    .uri("/api/v1/auth/login")
                    .bodyValue([username: username, password: password])
                    .exchange()

        then: "we expect the user to be authenticated and a token to be returned"
            1 * userInfoService.findUserByUsername(username) >> Mono.just(user)
            1 * passwordEncoder.matches(password, user.password) >> true
            1 * jwtService.generateToken(username) >> token

            result.expectStatus().isOk()
                    .expectBody()
                    .jsonPath('$.data.token').isEqualTo(token)
    }

    def "should return unauthorized for invalid credentials"() {
        given: "we have invalid user credentials"
            def username = "testUser"
            def password = "wrongPassword"

        when: "login endpoint is called with invalid credentials"
            def result = webTestClient
                    .post()
                    .uri("/api/v1/auth/login")
                    .bodyValue([username: username, password: password])
                    .exchange()

        then: "we expect an unauthorized response with an error message"
            1 * userInfoService.findUserByUsername(username) >> Mono.empty()
            0 * passwordEncoder.matches(_, _)
            0 * jwtService.generateToken(_)

            result.expectStatus().isUnauthorized()
                    .expectBody()
                    .jsonPath('$.success').isEqualTo(false)
                    .jsonPath('$.status').isEqualTo(401)
                    .jsonPath('$.data').isEqualTo("Invalid login credentials!")
    }

    def "should register user successfully"() {
        given: "we have user registration data"
            def username = "newUser"
            def password = "password"
            def displayName = "New User"
            def userId = "123"
            def request = new UserCreateRequest(username: username, password: password, displayName: displayName)
            def user = new User(id: userId, username: username, password: password, displayName: displayName, subscriptions: [])

        when: "register endpoint is called with valid user data"
            def result = webTestClient
                    .post()
                    .uri("/api/v1/auth/register")
                    .bodyValue(request)
                    .exchange()

        then: "we expect the user to be created and returned in the response"
            1 * userInfoService.createUser(_) >> { User u ->
                assert u.username == username
                assert u.password == password
                assert u.displayName == displayName
                return Mono.just(user)
            }

            result.expectStatus().isOk()
                    .expectBody()
                    .jsonPath('$.data.id').isEqualTo(userId)
                    .jsonPath('$.data.username').isEqualTo(username)
                    .jsonPath('$.data.displayName').isEqualTo(displayName)
    }
}
