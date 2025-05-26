package com.rudenko.socialmedia.service.post

import com.rudenko.socialmedia.data.entity.Post
import com.rudenko.socialmedia.data.entity.User
import com.rudenko.socialmedia.repository.post.PostLikeRepository
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification
import spock.lang.Subject

class PostLikeServiceSpec extends Specification {

    PostLikeRepository postLikeRepository = Mock()

    @Subject
    PostLikeService postLikeService

    def setup() {
        postLikeService = new PostLikeService(postLikeRepository)
    }

    def "should add like to post"() {
        given: "we want to like a post"
        def postId = "post123"
        def userId = "user123"
        def user = new User(id: userId)

        when: "add like method is called"
        def result = postLikeService.addLike(postId, user)

        and: "executed"
        StepVerifier.create(result)
                .expectNextMatches {
                    it.id == postId
                    it.likedBy == [userId]
                }
                .verifyComplete()

        then: "1 repository method is called"
        1 * postLikeRepository.addLikeFrom(userId, postId) >> Mono.just(new Post(id: postId, likedBy: [userId]))
    }

    def "should remove like from post"() {
        given: "we want to remove a like from the post"
        def postId = "post123"
        def userId = "user123"
        def user = new User(id: userId)

        when: "remove like method is called"
        def result = postLikeService.removeLike(postId, user)

        and: "executed"
        StepVerifier.create(result)
                .expectNextMatches {
                    it.id == postId
                    it.likedBy == []
                }
                .verifyComplete()

        then: "1 repository method is called"
        1 * postLikeRepository.removeLikeFrom(userId, postId) >> Mono.just(new Post(id: postId, likedBy: []))
    }
}
