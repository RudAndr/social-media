package com.rudenko.socialmedia.repository.post


import com.rudenko.socialmedia.data.entity.Post
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class PostGeneralRepository extends PostRepository {

    PostGeneralRepository(ReactiveMongoTemplate reactiveMongoTemplate) {
        super(reactiveMongoTemplate)
    }

    Flux<Post> getPostsFromUsers(Set<String> followedUserIds) {
        return reactiveMongoTemplate.find(
                new Query(this.getSubscribersCriteria(followedUserIds)),
                Post,
                "posts"
        )
    }

    Mono<Post> createPost(Post post) {
        return reactiveMongoTemplate.save(post, "posts")
    }

    Mono<Boolean> deletePost(String postId, String userId) {
        def query = new Query(findByPostIdCriteria(postId, userId))

        return reactiveMongoTemplate.remove(query, Post)
                .map {it.deletedCount > 0 }
    }

    Mono<Post> editPostText(String postId, String userId, String text) {
        def update = new Update().set("message_text", text)

        return updatePost(postId, userId, update)
    }
}
