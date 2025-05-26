package com.rudenko.socialmedia.repository.post

import com.rudenko.socialmedia.data.entity.Comment
import com.rudenko.socialmedia.data.entity.Post
import com.rudenko.socialmedia.repository.SpockDbIntegrationTest
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier

import java.time.LocalDateTime

class PostCommentRepositorySpec extends SpockDbIntegrationTest {

    @Autowired
    private PostCommentRepository postCommentRepository

    @Autowired
    private PostGeneralRepository postGeneralRepository

    def setup() {
        mongoContainer.execInContainer("mongo", "--eval", "rs.initiate()")
    }

    def "should add comment to a post"() {
        given: "we have a post"
        def post = new Post(
                userId: "user123",
                postedAt: LocalDateTime.now(),
                messageText: "Test post for adding comment"
        )
        def savedPost = postGeneralRepository.createPost(post).block()
        def userId = "user456"
        def commentText = "New comment"

        when: "add comment method called"
        def result = postCommentRepository.addComment(savedPost.id, userId, commentText)

        and: "executed"
        StepVerifier.create(result)
                .expectNextMatches {
                    it.postId == savedPost.id
                    it.comments.size() == 1
                    it.comments[0].userId == userId
                    it.comments[0].text == commentText
                }
                .verifyComplete()

        then:
        "Comment added successfully"
    }

    def "should remove comment from a post"() {
        given: "we have a post with comment"
        def userId = "user123"
        def commentId = "comment_to_remove"
        def post = new Post(
                userId: userId,
                postedAt: LocalDateTime.now(),
                messageText: "Test post for removing comment",
                comments: [
                        new Comment(id: commentId, userId: userId, postedAt: LocalDateTime.now(), text: "Comment to remove")
                ]
        )
        def savedPost = postGeneralRepository.createPost(post).block()

        when: "remove comment method is called"
        def result = postCommentRepository.removeComment(commentId, userId, savedPost.id)

        and: "executed"
        StepVerifier.create(result)
                .expectNextMatches {
                    it.postId == savedPost.id
                    it.comments.isEmpty()
                }
                .verifyComplete()

        then:
        "Comment removed successfully"
    }

    def "should handle getting comments for non-existent post"() {
        given: "we have non-existent postId"
        def nonExistentPostId = new ObjectId().toHexString()

        when: "get comments method is called"
        def result = postCommentRepository.getComments(nonExistentPostId)

        and: "executed, with 0 results"
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete()

        then:
        "No comments found for non-existent post"
    }

    def "should handle adding comment to non-existent post"() {
        given: "we have non-existent postId"
        def nonExistentPostId = new ObjectId().toHexString()
        def userId = "user123"
        def commentText = "Comment for non-existent post"

        when: "add comment method is called"
        def result = postCommentRepository.addComment(nonExistentPostId, userId, commentText)

        and: "executed, with 0 results"
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete()

        then:
        "Comment not added to non-existent post"
    }

    def "should handle removing non-existent comment"() {
        given: "we have post and non-existent commentId"
        def userId = "user123"
        def post = new Post(
                userId: userId,
                postedAt: LocalDateTime.now(),
                messageText: "Test post for removing non-existent comment"
        )
        def savedPost = postGeneralRepository.createPost(post).block()
        def nonExistentCommentId = "non_existent_comment_id"

        when: "remove comment method called"
        def result = postCommentRepository.removeComment(nonExistentCommentId, userId, savedPost.id)

        and: "executed, and nothing changed"
        StepVerifier.create(result)
                .expectNextMatches {
                    it.postId == savedPost.id
                    it.comments.isEmpty()
                }
                .verifyComplete()

        then:
        "Non-existent comment removal handled"
    }
}
