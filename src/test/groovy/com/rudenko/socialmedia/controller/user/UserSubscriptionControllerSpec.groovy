package com.rudenko.socialmedia.controller.user

import com.rudenko.socialmedia.controller.SpockApiIntegrationTest
import com.rudenko.socialmedia.service.user.UserSubscriptionService
import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import reactor.core.publisher.Mono

import static com.rudenko.socialmedia.config.GlobalValues.getGLOBAL_AUTH_TEST_USER_ID

class UserSubscriptionControllerSpec extends SpockApiIntegrationTest {

    @MockBean
    private UserSubscriptionService userSubscriptionService

    def "Should add subscription"() {
        given: "we have a target user ID to subscribe to"
        def userId = "test_id"

        when: "subscribe method is called"
        Mockito.when(userSubscriptionService.subscribe(Mockito.eq(GLOBAL_AUTH_TEST_USER_ID), Mockito.eq(userId))).thenReturn(Mono.just(true))

        then: "we expect the subscription to be added successfully"
        webTestClient
                .post()
                .uri("/api/v1/users/${userId}/subscribe")
                .header(HttpHeaders.AUTHORIZATION, "Bearer test_token")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath('$.success').isEqualTo(true)
                .jsonPath('$.status').isEqualTo(200)

    }

    def "Should remove subscription"() {
        given: "we have a target user ID to unsubscribe from"
        def userId = "test_id"

        when: "unsubscribe method is called"
        Mockito.when(userSubscriptionService.unsubscribe(Mockito.eq(GLOBAL_AUTH_TEST_USER_ID), Mockito.eq(userId))).thenReturn(Mono.just(true))

        then: "we expect the subscription to be removed successfully"
        webTestClient
                .delete()
                .uri("/api/v1/users/${userId}/subscribe")
                .header(HttpHeaders.AUTHORIZATION, "Bearer test_token")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath('$.success').isEqualTo(true)
                .jsonPath('$.status').isEqualTo(200)
    }


}
