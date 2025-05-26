package com.rudenko.socialmedia.controller

import com.rudenko.socialmedia.config.TestReactiveSecurityConfig
import com.rudenko.socialmedia.service.user.UserInfoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import spock.lang.Specification

@SpringBootTest
@ContextConfiguration(classes = TestReactiveSecurityConfig)
@AutoConfigureWebTestClient
abstract class SpockIntegrationTest extends Specification {

    @Autowired
    protected WebTestClient webTestClient

    @MockBean
    protected UserInfoService userInfoService
}
