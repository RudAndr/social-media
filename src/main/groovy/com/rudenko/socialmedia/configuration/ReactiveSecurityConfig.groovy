package com.rudenko.socialmedia.configuration


import com.rudenko.socialmedia.repository.user.UserInfoRepository
import com.rudenko.socialmedia.service.security.JwtService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.ServerSecurityContextRepository

@Configuration
@EnableWebFluxSecurity
class ReactiveSecurityConfig {

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                  ServerSecurityContextRepository jwtSecurityContextRepository) {
        return http
                .csrf {it.disable() }
                .httpBasic { it.disable() }
                .formLogin { it.disable() }
                .securityContextRepository(jwtSecurityContextRepository)
                .authorizeExchange {
                    it.pathMatchers("/api/v1/auth/**").permitAll()
                            .pathMatchers("/api/v1/users/**").hasRole("USER")
                            .anyExchange().authenticated()
                }
                .build()
    }

    @Bean
    ServerSecurityContextRepository jwtSecurityContextRepository(JwtService jwtService,
                                                                 UserInfoRepository userService) {
        return new JwtSecurityContextRepository(jwtService, userService)
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder()
    }
}
