package com.rudenko.socialmedia.config

import com.rudenko.socialmedia.data.entity.User
import com.rudenko.socialmedia.data.security.SocialUserDetails
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

import static com.rudenko.socialmedia.config.GlobalValues.GLOBAL_AUTH_TEST_USER_ID

class TestJwtSecurityContextRepository implements ServerSecurityContextRepository {
    private static final String TOKEN_PREFIX = "Bearer "

    @Override
    Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty()
    }

    @Override
    Mono<SecurityContext> load(ServerWebExchange exchange) {
        def authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION)

        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
            return Mono.empty()
        }

        return Mono.just(getTestContext())
    }

    private SecurityContext getTestContext() {
        def username = "test_user"
        def user = new User(id: GLOBAL_AUTH_TEST_USER_ID, username: username)

        def userDetails = new SocialUserDetails(username, user)

        def auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        auth.setDetails(user)

        return new SecurityContextImpl(auth)
    }
}
