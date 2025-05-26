package com.rudenko.socialmedia.configuration

import com.rudenko.socialmedia.data.security.SocialUserDetails
import com.rudenko.socialmedia.repository.user.UserInfoRepository
import com.rudenko.socialmedia.service.security.JwtService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


class JwtSecurityContextRepository implements ServerSecurityContextRepository {
    private static final String AUTH_HEADER_NAME = "Authorization"
    private static final String TOKEN_PREFIX = "Bearer "

    private final JwtService jwtService
    private final UserInfoRepository userRepository

    JwtSecurityContextRepository(JwtService jwtService, UserInfoRepository userRepository) {
        this.jwtService = jwtService
        this.userRepository = userRepository
    }

    @Override
    Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty()
    }

    @Override
    Mono<SecurityContext> load(ServerWebExchange exchange) {
        def authHeader = exchange.getRequest().getHeaders().getFirst(AUTH_HEADER_NAME)

        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
            return Mono.empty()
        }

        def token = authHeader.substring(TOKEN_PREFIX.length())

        if (!jwtService.isTokenValid(token)) {
            return Mono.empty()
        }

        def username = jwtService.extractUsername(token)

        return userRepository.findByUsername(username)
                .map {
                    def userDetails = new SocialUserDetails(username, it)
                    def auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
                    auth.setDetails(it)

                    return new SecurityContextImpl(auth)
                }
    }
}
