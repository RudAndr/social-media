package com.rudenko.socialmedia.controller

import com.rudenko.socialmedia.data.exception.InvalidCredentialsException
import com.rudenko.socialmedia.data.response.ApiResponse
import com.rudenko.socialmedia.data.response.TokenResponse
import com.rudenko.socialmedia.data.response.UserResponse
import com.rudenko.socialmedia.data.entity.User
import com.rudenko.socialmedia.data.request.user.LoginRequest
import com.rudenko.socialmedia.data.request.user.UserCreateRequest
import com.rudenko.socialmedia.service.user.UserInfoService
import com.rudenko.socialmedia.service.security.JwtService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/auth")
class AuthController {

    private final UserInfoService userService
    private final PasswordEncoder passwordEncoder
    private final JwtService jwtService

    AuthController(UserInfoService userService,
                   PasswordEncoder passwordEncoder,
                   JwtService jwtService) {
        this.userService = userService
        this.jwtService = jwtService
        this.passwordEncoder = passwordEncoder
    }

    @PostMapping("/register")
    Mono<ApiResponse<UserResponse>> createUser(@RequestBody UserCreateRequest request) {
        return userService.createUser(User.ofRequest(request))
                .map { new UserResponse(it.id, it.username, it.displayName) }
                .map { ApiResponse.wrapOkResponse(it) }
    }

    @PostMapping("/login")
    Mono<ApiResponse<TokenResponse>> login(@RequestBody LoginRequest request) {
        return userService.findUserByUsername(request.getUsername())
                .flatMap { user ->
                    if (passwordEncoder.matches(request.password, user.password)) {
                        def token = jwtService.generateToken(user.username)
                        return Mono.just(ApiResponse.wrapOkResponse(new TokenResponse(token)))
                    } else {
                        return Mono.error(new InvalidCredentialsException("Invalid login credentials!"))
                    }
                }
                .switchIfEmpty(Mono.error(new InvalidCredentialsException("Invalid login credentials!")))
    }
}
