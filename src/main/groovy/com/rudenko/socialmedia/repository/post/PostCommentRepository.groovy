package com.rudenko.socialmedia.repository.post

import com.rudenko.socialmedia.data.entity.Comment
import com.rudenko.socialmedia.data.entity.Post
import com.rudenko.socialmedia.data.response.CommentsResponse
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Repository
class PostCommentRepository extends PostRepository {
    protected PostCommentRepository(ReactiveMongoTemplate reactiveMongoTemplate) {
        super(reactiveMongoTemplate)
    }

    Flux<Comment> getComments(String postId) {
        def aggregation = Aggregation.newAggregation(
                Aggregation.match(findByPostIdCriteria(postId)),
                Aggregation.unwind("comments"),
                Aggregation.replaceRoot("comments")
        )

        return reactiveMongoTemplate.aggregate(aggregation, "posts", Comment)
    }

    Mono<CommentsResponse> addComment(String postId, String userId, String text) {
        def findCriteria = findByPostIdCriteria(postId)
        def commentId = new ObjectId().toHexString()

        def updateDefinition = new Update()
                .addToSet("comments", new Comment(
                        commentId,
                        userId,
                        LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                        text
                ))

        return reactiveMongoTemplate.findAndModify(
                new Query(findCriteria),
                updateDefinition,
                FindAndModifyOptions.options().returnNew(true),
                Post
        ).map {new CommentsResponse(it.id, it.comments) }
    }

    Mono<CommentsResponse> removeComment(String commentId, String userId, String postId) {
        def findCriteria = findByPostIdCriteria(postId)

        def updateDefinition = new Update()
                .pull("comments", [id: commentId, user_id: userId])

        return reactiveMongoTemplate.findAndModify(
                new Query(findCriteria),
                updateDefinition,
                FindAndModifyOptions.options().returnNew(true),
                Post
        ).map {new CommentsResponse(it.id, it.comments) }
    }
}
