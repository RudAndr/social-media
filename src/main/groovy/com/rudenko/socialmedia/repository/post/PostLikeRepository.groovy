package com.rudenko.socialmedia.repository.post

import com.rudenko.socialmedia.data.entity.Post
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class PostLikeRepository extends PostRepository {

    protected PostLikeRepository(ReactiveMongoTemplate reactiveMongoTemplate) {
        super(reactiveMongoTemplate)
    }

    Mono<Post> addLikeFrom(String userId, String postId) {
        def update = new Update().addToSet("liked_by", userId)

        return updatePost(postId, update)
    }

    Mono<Post> removeLikeFrom(String userId, String postId) {
        def update = new Update().pull("liked_by", userId)

        return updatePost(postId, update)
    }
}
