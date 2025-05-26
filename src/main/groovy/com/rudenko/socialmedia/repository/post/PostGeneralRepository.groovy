package com.rudenko.socialmedia.repository.post

import com.rudenko.socialmedia.data.entity.Comment
import com.rudenko.socialmedia.data.entity.Post
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class PostRepository {

    private final ReactiveMongoTemplate reactiveMongoTemplate

    PostRepository(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate
    }

    //todo for future
    Flux<Post> streamPostsForSubscribers(Set<String> followedUserIds) {
        return reactiveMongoTemplate.changeStream(Post)
                .watchCollection("posts")
                .filter(this.getSubscribersCriteria(followedUserIds))
                .listen()
                .cast(Post)
    }

    Flux<Comment> streamPostComments(String postId) {
        return reactiveMongoTemplate.changeStream()
    }

    Mono<Post> createPost(Post post) {
        return reactiveMongoTemplate.save(post, "posts")
    }

    Mono<Boolean> deletePost(String postId) {
        def query = new Query(Criteria.where("_id").is(postId))

        return reactiveMongoTemplate.remove(query, Post)
                .map {it.deletedCount > 0 }
    }

    Mono<Post> addLikeFrom(String userId, String postId) {
        def update = new Update().addToSet("liked_by", userId)

        return updatePost(postId, update)
    }

    Mono<Post> removeLikeFrom(String userId, String postId) {
        def update = new Update().pull("liked_by", userId)

        return updatePost(postId, update)
    }

    Mono<Post> editPostText(String postId, String text) {
        def update = new Update().set("message_text", text)

        return updatePost(postId, update)
    }

    private Mono<Post> updatePost(String postId, Update updateDefinition) {
        def query = new Query(Criteria.where("_id").is(postId))

        return reactiveMongoTemplate.findAndModify(query, updateDefinition, Post)
    }

    private Criteria getSubscribersCriteria(Set<String> followers) {
        return Criteria.where("target_id").in(followers)
    }
}
