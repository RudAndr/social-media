package com.rudenko.socialmedia.controller.post


import com.rudenko.socialmedia.controller.SpockApiIntegrationTest
import com.rudenko.socialmedia.data.entity.Post
import com.rudenko.socialmedia.data.request.post.EditPostRequest
import com.rudenko.socialmedia.data.response.PostResponse
import com.rudenko.socialmedia.service.post.PostGeneralService
import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

import static com.rudenko.socialmedia.config.GlobalValues.getGLOBAL_AUTH_TEST_USER_ID


class PostGeneralControllerSpec extends SpockApiIntegrationTest {

    @MockBean
    private PostGeneralService postGeneralService

    def "Should return posts successfully"() {
        given: "we have a user with posts"
        def post = new Post(id: "123", userId: GLOBAL_AUTH_TEST_USER_ID)

        when: "getPosts method is called"
        Mockito.when(postGeneralService.getPosts(Mockito.eq(GLOBAL_AUTH_TEST_USER_ID), Mockito.any())).thenReturn(Flux.just(post))

        then: "we expect the posts to be returned successfully"
        webTestClient
                .get()
                .uri("/api/v1/posts/${GLOBAL_AUTH_TEST_USER_ID}")
                .header(HttpHeaders.AUTHORIZATION, "Bearer test_token")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath('$.[0].success').isEqualTo(true)
                .jsonPath('$.[0].status').isEqualTo(200)
                .jsonPath('$.[0].data.id').isEqualTo(post.id)
                .jsonPath('$.[0].data.userId').isEqualTo(GLOBAL_AUTH_TEST_USER_ID)

    }

    def "Should create posts successfully"() {
        given: "we have post data to create a new post"
        def postId = "123"
        def post = new Post(id: postId, userId: GLOBAL_AUTH_TEST_USER_ID, messageText: "Text")
        def postRequest = new PostResponse(id: postId, messageText: "Text")

        when: "createPost method is called"
        Mockito.when(postGeneralService.createPost(Mockito.any())).thenReturn(Mono.just(post))

        then: "we expect the post to be created successfully"
        webTestClient
                .post()
                .uri("/api/v1/posts")
                .header(HttpHeaders.AUTHORIZATION, "Bearer test_token")
                .bodyValue(postRequest)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath('$.success').isEqualTo(true)
                .jsonPath('$.status').isEqualTo(200)
                .jsonPath('$.data.id').isEqualTo(postId)
                .jsonPath('$.data.messageText').isEqualTo("Text")
    }

    def "Should delete posts successfully"() {
        given: "we have a post ID to delete"
        def postId = "123"

        when: "deletePost method is called"
        Mockito.when(postGeneralService.deletePost(postId, GLOBAL_AUTH_TEST_USER_ID)).thenReturn(Mono.just(true))

        then: "we expect the post to be deleted successfully"
        webTestClient
                .delete()
                .uri("/api/v1/posts/${postId}")
                .header(HttpHeaders.AUTHORIZATION, "Bearer test_token")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath('$.success').isEqualTo(true)
                .jsonPath('$.status').isEqualTo(200)
    }

    def "Should edit posts successfully"() {
        given: "we have a post ID and new content to update the post"
        def postId = "123"
        def userId = "123"
        def post = new Post(id: postId, userId: userId)
        def editPostRequest = new EditPostRequest("Some text")

        when: "editPost method is called"
        Mockito.when(postGeneralService.editPost(Mockito.eq(postId), Mockito.any(), Mockito.eq(GLOBAL_AUTH_TEST_USER_ID))).thenReturn(Mono.just(post))

        then: "we expect the post to be updated successfully"
        webTestClient
                .put()
                .uri("/api/v1/posts/${postId}")
                .header(HttpHeaders.AUTHORIZATION, "Bearer test_token")
                .bodyValue(editPostRequest)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath('$.success').isEqualTo(true)
                .jsonPath('$.status').isEqualTo(200)
                .jsonPath('$.data.id').isEqualTo(postId)
    }
}
