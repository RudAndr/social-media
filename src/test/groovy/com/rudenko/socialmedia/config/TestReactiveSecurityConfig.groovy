package com.rudenko.socialmedia.config


import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.web.server.context.ServerSecurityContextRepository

@TestConfiguration
class TestReactiveSecurityConfig {

    @Bean
    @Primary
    ServerSecurityContextRepository testJwtSecurityContextRepository() {
        return new TestJwtSecurityContextRepository()
    }

}
