package com.rudenko.socialmedia.controller.post

import com.rudenko.socialmedia.controller.SpockApiIntegrationTest
import com.rudenko.socialmedia.data.entity.Post
import com.rudenko.socialmedia.service.post.PostLikeService
import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import reactor.core.publisher.Mono

import static com.rudenko.socialmedia.config.GlobalValues.getGLOBAL_AUTH_TEST_USER_ID

class PostLikeControllerSpec extends SpockApiIntegrationTest {

    @MockBean
    private PostLikeService postLikeService

    def "Should add like"() {
        given: "we have a post ID to like"
        def postId = "123"
        def post = new Post(id: postId, userId: GLOBAL_AUTH_TEST_USER_ID, messageText: "Text", likedBy: ["other_user"])

        when: "addLike method is called"
        Mockito.when(postLikeService.addLike(Mockito.eq(postId), Mockito.any())).thenReturn(Mono.just(post))

        then: "we expect the like to be added successfully"
        webTestClient
                .post()
                .uri("/api/v1/posts/${postId}/like")
                .header(HttpHeaders.AUTHORIZATION, "Bearer test_token")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath('$.success').isEqualTo(true)
                .jsonPath('$.status').isEqualTo(200)
                .jsonPath('$.data.id').isEqualTo(postId)
                .jsonPath('$.data.likes[0]').isEqualTo("other_user")
    }

    def "Should remove like"() {
        given: "we have a post ID to unlike"
        def postId = "123"
        def post = new Post(id: postId, userId: GLOBAL_AUTH_TEST_USER_ID, messageText: "Text", likedBy: [])

        when: "removeLike method is called"
        Mockito.when(postLikeService.removeLike(Mockito.eq(postId), Mockito.any())).thenReturn(Mono.just(post))

        then: "we expect the like to be removed successfully"
        webTestClient
                .delete()
                .uri("/api/v1/posts/${postId}/like")
                .header(HttpHeaders.AUTHORIZATION, "Bearer test_token")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath('$.success').isEqualTo(true)
                .jsonPath('$.status').isEqualTo(200)
                .jsonPath('$.data.id').isEqualTo(postId)
                .jsonPath('$.data.likes').isEmpty()
    }

}
