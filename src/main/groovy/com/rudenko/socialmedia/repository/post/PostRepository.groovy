package com.rudenko.socialmedia.repository.post

import com.rudenko.socialmedia.data.entity.Post
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import reactor.core.publisher.Mono

abstract class PostRepository {

    protected final ReactiveMongoTemplate reactiveMongoTemplate

    protected PostRepository(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate
    }

    protected Mono<Post> updatePost(String postId, Update updateDefinition) {
        def query = new Query(findByPostIdCriteria(postId))

        return reactiveMongoTemplate.findAndModify(
                query,
                updateDefinition,
                FindAndModifyOptions.options().returnNew(true),
                Post
        )
    }

    protected Mono<Post> updatePost(String postId, String userId, Update updateDefinition) {
        def query = new Query(findByPostIdCriteria(postId, userId))

        return reactiveMongoTemplate.findAndModify(
                query,
                updateDefinition,
                FindAndModifyOptions.options().returnNew(true),
                Post
        )
    }

    protected Criteria getSubscribersCriteria(Set<String> followers) {
        return Criteria.where("user_id").in(followers)
    }

    protected Criteria findByPostIdCriteria(String postId) {
        return Criteria.where("_id").is(postId)
    }

    protected Criteria findByPostIdCriteria(String postId, String userId) {
        return Criteria.where("_id").is(postId).and("user_id").is(userId)
    }
}
