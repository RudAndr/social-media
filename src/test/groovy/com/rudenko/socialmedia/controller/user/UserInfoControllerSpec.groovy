package com.rudenko.socialmedia.controller.user

import com.rudenko.socialmedia.controller.SpockApiIntegrationTest
import com.rudenko.socialmedia.data.entity.User
import com.rudenko.socialmedia.data.request.user.UserEditAction
import com.rudenko.socialmedia.data.request.user.UserEditRequest
import com.rudenko.socialmedia.service.user.UserInfoService
import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import reactor.core.publisher.Mono

import static com.rudenko.socialmedia.config.GlobalValues.getGLOBAL_AUTH_TEST_USER_ID

class UserInfoControllerSpec extends SpockApiIntegrationTest {

    @MockBean
    private UserInfoService userInfoService

    def "Should get user"() {
        given: "we have a user with a specific ID"
        def user = new User(id: GLOBAL_AUTH_TEST_USER_ID)

        when: "findUserById method is called"
        Mockito.when(userInfoService.findUserById(GLOBAL_AUTH_TEST_USER_ID)).thenReturn(Mono.just(user))

        then: "we expect the user information to be returned successfully"
        webTestClient
                .get()
                .uri("/api/v1/users/${GLOBAL_AUTH_TEST_USER_ID}/info")
                .header(HttpHeaders.AUTHORIZATION, "Bearer test_token")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath('$.success').isEqualTo(true)
                .jsonPath('$.status').isEqualTo(200)
                .jsonPath('$.data.id').isEqualTo(GLOBAL_AUTH_TEST_USER_ID)

    }

    def "Should should delete user"() {
        given: "we have a user ID to delete"
        def userId = GLOBAL_AUTH_TEST_USER_ID

        when: "deleteUser method is called"
        Mockito.when(userInfoService.deleteUser(Mockito.eq(userId), Mockito.any())).thenReturn(Mono.just(true))

        then: "we expect the user to be deleted successfully"
        webTestClient
                .delete()
                .uri("/api/v1/users/${userId}/info")
                .header(HttpHeaders.AUTHORIZATION, "Bearer test_token")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath('$.success').isEqualTo(true)
                .jsonPath('$.status').isEqualTo(200)
    }

    def "Should edit user"() {
        given: "we have a user and a request to change their username"
        def newUserName = "NewUser"
        def user = new User(id: GLOBAL_AUTH_TEST_USER_ID, username: newUserName)
        def editRequest = new UserEditRequest(UserEditAction.USERNAME_CHANGE, newUserName)

        when: "editUser method is called"
        Mockito.when(userInfoService.editUser(Mockito.eq(GLOBAL_AUTH_TEST_USER_ID), Mockito.any(), Mockito.any())).thenReturn(Mono.just(user))

        then: "we expect the user to be updated with the new username"
        webTestClient
                .put()
                .uri("/api/v1/users/${GLOBAL_AUTH_TEST_USER_ID}/info")
                .header(HttpHeaders.AUTHORIZATION, "Bearer test_token")
                .bodyValue(editRequest)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath('$.success').isEqualTo(true)
                .jsonPath('$.status').isEqualTo(200)
                .jsonPath('$.data.id').isEqualTo(GLOBAL_AUTH_TEST_USER_ID)
                .jsonPath('$.data.username').isEqualTo(newUserName)
    }

}
