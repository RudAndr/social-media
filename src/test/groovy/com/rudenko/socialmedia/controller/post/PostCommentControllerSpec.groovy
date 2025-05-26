package com.rudenko.socialmedia.controller.post


import com.rudenko.socialmedia.controller.SpockApiIntegrationTest
import com.rudenko.socialmedia.data.entity.Comment
import com.rudenko.socialmedia.data.response.CommentResponse
import com.rudenko.socialmedia.data.response.CommentsResponse
import com.rudenko.socialmedia.service.post.PostCommentService
import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

import java.time.LocalDateTime

import static com.rudenko.socialmedia.config.GlobalValues.getGLOBAL_AUTH_TEST_USER_ID

class PostCommentControllerSpec extends SpockApiIntegrationTest {

    @MockBean
    private PostCommentService postCommentService

    def "should post a comment successfully"() {
        given: "PostId, userId and comment text. Here we mocking postCommentService.createComment, as we just testing request"
        def postId = "6832fe5efbdf381ae36efe84"
        def commentText = "This is a test comment"
        def comments = [new Comment(id: "comment1", userId: GLOBAL_AUTH_TEST_USER_ID, postedAt: LocalDateTime.now(), text: commentText)]
        def commentsResponse = new CommentsResponse(postId, comments)

        when: "postCommentService.createComment returns Mono of our created comment"
        Mockito.when(postCommentService.createComment(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Mono.just(commentsResponse))

        then: "Successful response, and correct values"
        webTestClient
                .post()
                .uri("/api/v1/posts/${postId}/comments")
                .header(HttpHeaders.AUTHORIZATION, "Bearer test_token")
                .bodyValue([text: commentText])
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath('$.success').isEqualTo(true)
                .jsonPath('$.status').isEqualTo(200)
                .jsonPath('$.data.postId').isEqualTo(postId)
                .jsonPath('$.data.comments[0].id').isEqualTo("comment1")
                .jsonPath('$.data.comments[0].userId').isEqualTo(GLOBAL_AUTH_TEST_USER_ID)
                .jsonPath('$.data.comments[0].text').isEqualTo(commentText)
    }

    def "should delete a comment successfully"() {
        given: "we have a post ID and a comment ID to delete"
        def postId = "post123"
        def commentId = "comment1"
        def comments = [new Comment(id: "comment2", userId: GLOBAL_AUTH_TEST_USER_ID, postedAt: LocalDateTime.now(), text: "Another comment")]
        def commentsResponse = new CommentsResponse(postId, comments)

        when: "deleteComment method is called"
        Mockito.when(postCommentService.deleteComment(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Mono.just(commentsResponse))

        then: "we expect the comment to be deleted successfully"
        webTestClient
                .delete()
                .uri("/api/v1/posts/${postId}/comments/${commentId}")
                .header(HttpHeaders.AUTHORIZATION, "Bearer test_token")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath('$.success').isEqualTo(true)
                .jsonPath('$.status').isEqualTo(200)
                .jsonPath('$.data.postId').isEqualTo(postId)
                .jsonPath('$.data.comments[0].id').isEqualTo("comment2")
    }

    def "should get comments for a post"() {
        given: "we have a post ID and some comments for that post"
        def postId = "post123"

        def comment1 = new Comment(id: "comment", userId: GLOBAL_AUTH_TEST_USER_ID, postedAt: LocalDateTime.now(), text: "Comment")
        def comment2 = new Comment(id: "comment", userId: GLOBAL_AUTH_TEST_USER_ID, postedAt: LocalDateTime.now(), text: "Comment")
        def commentResponse1 = new CommentResponse(postId, comment1)
        def commentResponse2 = new CommentResponse(postId, comment2)

        when: "getComments method is called"
        Mockito.when(postCommentService.getComments(postId)).thenReturn(Flux.just(commentResponse1, commentResponse2))

        then: "we expect the comments to be returned successfully"
        webTestClient
                .get()
                .uri("/api/v1/posts/${postId}/comments")
                .header(HttpHeaders.AUTHORIZATION, "Bearer test_token")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath('$.[0].success').isEqualTo(true)
                .jsonPath('$.[0].status').isEqualTo(200)
                .jsonPath('$.[0].data.postId').isEqualTo(postId)
                .jsonPath('$.[0].data.comment.id').isEqualTo("comment")
                .jsonPath('$.[0].data.comment.userId').isEqualTo(GLOBAL_AUTH_TEST_USER_ID)
                .jsonPath('$.[0].data.comment.text').isEqualTo("Comment")
    }
}
