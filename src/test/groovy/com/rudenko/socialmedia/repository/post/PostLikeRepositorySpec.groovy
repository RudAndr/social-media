package com.rudenko.socialmedia.repository.post

import com.rudenko.socialmedia.data.entity.Post
import com.rudenko.socialmedia.repository.SpockDbIntegrationTest
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier

import java.time.LocalDateTime

class PostLikeRepositorySpec extends SpockDbIntegrationTest {

    @Autowired
    private PostLikeRepository postLikeRepository

    @Autowired
    private PostGeneralRepository postGeneralRepository

    def setup() {
        mongoContainer.execInContainer("mongo", "--eval", "rs.initiate()")
    }

    def "should add like to post"() {
        given: "user with id wants to like post, that`s exists in DB"
        def userId = "user123"
        def post = new Post(
                userId: "post_owner",
                postedAt: LocalDateTime.now(),
                messageText: "Test post for adding like"
        )
        def savedPost = postGeneralRepository.createPost(post).block()

        when: "addLike method is called"
        def result = postLikeRepository.addLikeFrom(userId, savedPost.id)

        and: "executed"
        StepVerifier.create(result)
                .expectNextMatches {
                    it.id == savedPost.id
                    it.likedBy.contains(userId)
                    it.likedBy.size() == 1
                }
                .verifyComplete()

        then:
        "Like added successfully"
    }

    def "should remove like from post"() {
        given: "user with id wants to remove like from post, that`s exists in DB"
        def userId = "user123"
        def post = new Post(
                userId: "post_owner",
                postedAt: LocalDateTime.now(),
                messageText: "Test post for removing like"
        )
        def savedPost = postGeneralRepository.createPost(post).block()

        postLikeRepository.addLikeFrom(userId, savedPost.id).block()

        when: "remove method is called"
        def result = postLikeRepository.removeLikeFrom(userId, savedPost.id)

        and: "executed"
        StepVerifier.create(result)
                .expectNextMatches {
                    it.id == savedPost.id
                    !it.likedBy.contains(userId)
                    it.likedBy.isEmpty()
                }
                .verifyComplete()

        then:
        "Like removed successfully"
    }

    def "should handle adding like to non-existent post"() {
        given: "user with id wants to like post, that`s do not exist in DB"
        def userId = "user123"
        def nonExistentPostId = new ObjectId().toHexString()

        when: "addLike method is called"
        def result = postLikeRepository.addLikeFrom(userId, nonExistentPostId)

        and: "executed with 0 update count"
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete()

        then:
        "Adding like to non-existent post handled"
    }

    def "should handle removing like from non-existent post"() {
        given: "user with id wants to remove like from post, that`s do not exists in DB"
        def userId = "user123"
        def nonExistentPostId = new ObjectId().toHexString()

        when:  "remove method is called"
        def result = postLikeRepository.removeLikeFrom(userId, nonExistentPostId)

        and: "executed with 0 update count"
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete()

        then:
        "Removing like from non-existent post handled"
    }

    def "should handle adding duplicate like"() {
        given: "user with id wants to add a second like to a post, that`s exists in DB"
        def userId = "user123"
        def post = new Post(
                userId: "post_owner",
                postedAt: LocalDateTime.now(),
                messageText: "Test post for duplicate like"
        )
        def savedPost = postGeneralRepository.createPost(post).block()

        postLikeRepository.addLikeFrom(userId, savedPost.id).block()

        when: "addLike is called"
        def result = postLikeRepository.addLikeFrom(userId, savedPost.id)

        and: "executed, but like count is not changed"
        StepVerifier.create(result)
                .expectNextMatches {
                    it.id == savedPost.id
                    it.likedBy.contains(userId)
                    it.likedBy.size() == 1
                }
                .verifyComplete()

        then:
        "Duplicate like handled correctly"
    }

    def "should handle removing non-existent like"() {
        given: "user with id wants to remove non-existent like to a post, that`s exists in DB"
        def userId = "user123"
        def nonExistentUserId = "nonexistent_user"
        def post = new Post(
                userId: "post_owner",
                postedAt: LocalDateTime.now(),
                messageText: "Test post for removing non-existent like"
        )
        def savedPost = postGeneralRepository.createPost(post).block()

        postLikeRepository.addLikeFrom(userId, savedPost.id).block()

        when: "removeLike is called"
        def result = postLikeRepository.removeLikeFrom(nonExistentUserId, savedPost.id)

        and: "executed, but nothing changed in DB"
        StepVerifier.create(result)
                .expectNextMatches {
                    it.id == savedPost.id
                    it.likedBy.contains(userId)
                    it.likedBy.size() == 1
                }
                .verifyComplete()

        then:
        "Removing non-existent like handled correctly"
    }
}
