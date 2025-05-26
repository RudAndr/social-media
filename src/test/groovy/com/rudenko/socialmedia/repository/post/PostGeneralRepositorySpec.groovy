package com.rudenko.socialmedia.repository.post

import com.rudenko.socialmedia.data.entity.Post
import com.rudenko.socialmedia.repository.SpockDbIntegrationTest
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier

import java.time.LocalDateTime

class PostGeneralRepositorySpec extends SpockDbIntegrationTest {

    @Autowired
    private PostGeneralRepository postGeneralRepository

    def setup() {
        mongoContainer.execInContainer("mongo", "--eval", "rs.initiate()")
    }

    def "should stream posts for subscribers"() {
        given: "we have three different posts from 3 subs"
        def user1Id = "user666"
        def user2Id = "user777"
        def user3Id = "user888"

        def post1 = new Post(
                userId: user1Id,
                postedAt: LocalDateTime.now(),
                messageText: "Post from user 1"
        )
        def post2 = new Post(
                userId: user2Id,
                postedAt: LocalDateTime.now(),
                messageText: "Post from user 2"
        )
        def post3 = new Post(
                userId: user3Id,
                postedAt: LocalDateTime.now(),
                messageText: "Post from user 3"
        )

        postGeneralRepository.createPost(post1).block()
        postGeneralRepository.createPost(post2).block()
        postGeneralRepository.createPost(post3).block()

        def followedUserIds = [user1Id] as Set

        when: "getPosts method called"
        def result = postGeneralRepository.getPostsFromUsers(followedUserIds)

        and: "executed"
        StepVerifier.create(result)
                .expectNextMatches {
                    it.userId == user1Id
                    it.messageText == "Post from user 1"
                }
                .expectNextCount(0)
                .verifyComplete()

        then:
        "Posts streamed successfully for 3 subscribers"
    }

    def "should create post"() {
        given: "we want to save post"
        def post = new Post(
                userId: "user123",
                postedAt: LocalDateTime.now(),
                messageText: "Test post creation"
        )

        when: "createPost is called"
        def result = postGeneralRepository.createPost(post)

        and: "executed"
        StepVerifier.create(result)
                .expectNextMatches {
                    it.id != null
                    it.userId == "user123"
                    it.messageText == "Test post creation"
                }
                .verifyComplete()

        then:
        "Post created successfully"
    }

    def "should delete post"() {
        given: "we have a post, we want to delete"
        def userId = "user123"
        def post = new Post(
                userId: userId,
                postedAt: LocalDateTime.now(),
                messageText: "Test post for deletion"
        )
        def savedPost = postGeneralRepository.createPost(post).block()

        when: "deletePost is called"
        def result = postGeneralRepository.deletePost(savedPost.id, userId)

        and: "executed"
        StepVerifier.create(result)
                .expectNextMatches {
                    it == true
                }
                .verifyComplete()

        then:
        "Post deleted successfully"
    }

    def "should edit post text"() {
        given: "We have a post, we want to edit"
        def userId = "user123"
        def post = new Post(
                userId: userId,
                postedAt: LocalDateTime.now(),
                messageText: "Original post text"
        )
        def savedPost = postGeneralRepository.createPost(post).block()
        def newText = "Updated post text"

        when: "edit method is called"
        def result = postGeneralRepository.editPostText(savedPost.id, userId, newText)

        and: "executed"
        StepVerifier.create(result)
                .expectNextMatches {
                    it.id == savedPost.id
                    it.userId == "user123"
                    it.messageText == newText
                }
                .verifyComplete()

        then:
        "Post text edited successfully"
    }

    def "should handle streaming posts with empty subscribers list"() {
        given: "we trying to get posts for 0 users"
        def emptyFollowedUserIds = [] as Set

        when: "getPosts is called"
        def result = postGeneralRepository.getPostsFromUsers(emptyFollowedUserIds)

        and: "executed with 0 results"
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete()

        then:
        "Empty subscribers list handled correctly"
    }

    def "should handle deleting non-existent post"() {
        given: "we have non-existent userId"
        def userId = "user123"
        def nonExistentPostId = new ObjectId().toHexString()

        when: "deleteMethod is called"
        def result = postGeneralRepository.deletePost(nonExistentPostId, userId)

        and: "executed, with 0 changes"
        StepVerifier.create(result)
                .expectNextMatches {
                    it == false
                }
                .verifyComplete()

        then:
        "Non-existent post deletion handled"
    }

    def "should handle editing non-existent post"() {
        given: "we have non-existent userId"
        def userId = "user123"
        def nonExistentPostId = new ObjectId().toHexString()
        def newText = "Updated text for non-existent post"

        when: "edit method is called"
        def result = postGeneralRepository.editPostText(nonExistentPostId, userId, newText)

        and: "executed, with 0 changes"
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete()

        then:
        "Non-existent post editing handled"
    }
}
