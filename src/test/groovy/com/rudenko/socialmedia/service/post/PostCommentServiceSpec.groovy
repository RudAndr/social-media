package com.rudenko.socialmedia.service.post

import com.rudenko.socialmedia.data.entity.Comment
import com.rudenko.socialmedia.data.response.CommentsResponse
import com.rudenko.socialmedia.repository.post.PostCommentRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification
import spock.lang.Subject

class PostCommentServiceSpec extends Specification {

    PostCommentRepository postCommentRepository

    @Subject
    PostCommentService postCommentService

    def setup() {
        postCommentRepository = Mock(PostCommentRepository)
        postCommentService = new PostCommentService(postCommentRepository)
    }

    def "should get comments for a post"() {
        given: "we have postId"
        def postId = "post123"

        when: "get comments method is called"
        def result = postCommentService.getComments(postId)

        and: "executed"
        StepVerifier.create(result)
                .expectNextMatches {
                    it.postId == postId
                    it.comment.id == "comment1"
                    it.comment.text == "Comment 1"
                }.expectNextMatches {
                    it.postId == postId
                    it.comment.id == "comment2"
                    it.comment.text == "Comment 2"
                }
                .verifyComplete()

        then: "we expect 1 repository call"
        1 * postCommentRepository.getComments(postId) >> Flux.fromIterable([
                new Comment(id: "comment1", userId: "user123", text: "Comment 1"),
                new Comment(id: "comment2", userId: "user456", text: "Comment 2")
        ])
    }

    def "should create a comment"() {
        given: "we have a comment, we want to save"
        def postId = "post123"
        def userId = "user123"
        def text = "New comment"

        when: "create comment method is called"
        def result = postCommentService.createComment(postId, userId, text)

        and: "executed"
        StepVerifier.create(result)
                .expectNextMatches {
                    it.postId == postId
                    it.comments.size() == 1
                    it.comments[0].userId == userId
                    it.comments[0].text == text
                }
                .verifyComplete()

        then: "we expect 1 repository call"
        1 * postCommentRepository.addComment(postId, userId, text) >> Mono.just(new CommentsResponse(postId, [
                new Comment(id: "comment1", userId: userId, text: text)
        ]))
    }

    def "should delete a comment"() {
        given: "we have a comment we want to delete"
        def postId = "post123"
        def commentId = "comment1"
        def userId = "user123"
        def commentsResponse = new CommentsResponse(postId: postId, comments: [])

        when: "delete method is called"
        def result = postCommentService.deleteComment(postId, commentId, userId)

        and: "executed"
        StepVerifier.create(result)
                .expectNextMatches {
                    it.postId == postId
                    it.comments.isEmpty()
                }
                .verifyComplete()

        then: "we expect 1 repository call"
        1 * postCommentRepository.removeComment(commentId, userId, postId) >> Mono.just(commentsResponse)
    }

    def "should handle empty comments list"() {
        given: "we have postId that`s not saved in db"
        def postId = "post123"

        when: "getComments method called"
        def result = postCommentService.getComments(postId)

        and: "executed with empty list"
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete()

        then: "we expect 1 repository call"
        1 * postCommentRepository.getComments(postId) >> Flux.empty()
    }
}
