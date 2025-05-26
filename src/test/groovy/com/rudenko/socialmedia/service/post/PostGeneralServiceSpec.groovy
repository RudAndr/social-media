package com.rudenko.socialmedia.service.post

import com.rudenko.socialmedia.data.entity.Post
import com.rudenko.socialmedia.data.entity.User
import com.rudenko.socialmedia.data.request.post.EditPostRequest
import com.rudenko.socialmedia.repository.post.PostGeneralRepository
import com.rudenko.socialmedia.service.user.UserInfoService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification
import spock.lang.Subject

class PostGeneralServiceSpec extends Specification {

    PostGeneralRepository postRepository = Mock()
    UserInfoService userService = Mock()

    @Subject
    PostGeneralService postGeneralService

    def setup() {
        postGeneralService = new PostGeneralService(postRepository, userService)
    }

    def "should get posts for current user"() {
        given: "we have want to get our own posts"
        def userId = "user123"
        def currentUser = new User(id: userId, subscriptions: ["user456", "user789"])

        when: "get posts is called"
        def result = postGeneralService.getPosts(userId, currentUser)

        and: "executed"
        StepVerifier.create(result)
                .expectNextMatches {
                    it.id == "post1"
                    it.messageText == "Post 1"
                }.expectNextMatches {
                    it.id == "post2"
                    it.messageText == "Post 2"
                }.expectNextMatches {
                    it.id == "post3"
                    it.messageText == "Post 3"
                }
                .verifyComplete()

        then: "we expect 1 repository call"

        1 * postRepository.getPostsFromUsers({ Set<String> users ->
            users.containsAll([userId, "user456", "user789"])
        }) >> Flux.fromIterable([
                new Post(id: "post1", userId: userId, messageText: "Post 1"),
                new Post(id: "post2", userId: "user456", messageText: "Post 2"),
                new Post(id: "post3", userId: "user789", messageText: "Post 3")
        ])
    }

    def "should get posts for another user"() {
        given: "we have another user, whose posts we want to see"
        def userId = "user456"
        def currentUser = new User(id: "user123", subscriptions: ["user456", "user789"])

        when: "get posts method is called"
        def result = postGeneralService.getPosts(userId, currentUser)

        and: "executed"
        StepVerifier.create(result)
                .expectNextMatches {
                    it.id == "post2"
                    it.messageText == "Post 2"
                }
                .verifyComplete()

        then: "we expect 2 repository calls: to find user, and his posts"
        1 * userService.findUserById(userId) >> Mono.just(new User(id: userId, subscriptions: ["user789"]))

        1 * postRepository.getPostsFromUsers({ Set<String> users ->
            users.contains(userId) && users.size() == 1
        }) >> Flux.fromIterable([new Post(id: "post2", userId: userId, messageText: "Post 2")])
    }

    def "should create post"() {
        given: "we want to save a post"
        def post = new Post(id: "post1", userId: "user123", messageText: "New post")

        when: "create post method is called"
        def result = postGeneralService.createPost(post)

        and: "executed"
        StepVerifier.create(result)
                .expectNextMatches {
                    it.id == "post1"
                    it.userId == "user123"
                    it.messageText == "New post"
                }
                .verifyComplete()

        then: "we expect 1 repository call"
        1 * postRepository.createPost(post) >> Mono.just(post)
    }

    def "should edit post"() {
        given: "we have a post we want to edit"
        def userId = "user123"
        def postId = "post1"
        def newText = "Updated post text"
        def editRequest = new EditPostRequest(text: newText)

        when: "edit post is called"
        def result = postGeneralService.editPost(postId, editRequest, userId)

        and: "executed"
        StepVerifier.create(result)
                .expectNextMatches {
                    it.id == postId
                    it.userId == userId
                    it.messageText == newText
                }
                .verifyComplete()

        then: "1 repository method is called"
        1 * postRepository.editPostText(postId, userId, newText) >> Mono.just(new Post(id: postId, userId: "user123", messageText: newText))
    }

    def "should delete post"() {
        given: "we want to delete post"
        def userId = "123"
        def postId = "post1"

        when: "delete post method is called"
        def result = postGeneralService.deletePost(postId, userId)

        and: "executed"
        StepVerifier.create(result)
                .expectNextMatches {
                    it == true
                }
                .verifyComplete()

        then: "1 repository method is called"
        1 * postRepository.deletePost(postId, userId) >> Mono.just(true)
    }
}
