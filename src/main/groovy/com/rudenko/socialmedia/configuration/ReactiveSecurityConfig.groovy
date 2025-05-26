package com.rudenko.socialmedia.configuration

import com.rudenko.socialmedia.repository.user.UserInfoRepository
import com.rudenko.socialmedia.service.security.JwtService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
@Profile("!servlet")
class ReactiveSecurityConfig {

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                 JwtSecurityContextRepository securityContextRepository) {
        return http
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .securityContextRepository(securityContextRepository)
                .authorizeExchange()
                .pathMatchers("/api/v1/auth/**").permitAll()
                .pathMatchers("/api/v1/users/**").hasRole("USER")
                .anyExchange().authenticated()
                .and()
                .build()
    }

    @Bean
    JwtSecurityContextRepository jwtSecurityContextRepository(JwtService jwtService,
                                                             UserInfoRepository userService) {
        return new JwtSecurityContextRepository(jwtService, userService)
    }
}
