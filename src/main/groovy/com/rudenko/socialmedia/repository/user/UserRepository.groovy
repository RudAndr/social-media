package com.rudenko.socialmedia.repository.user

import com.rudenko.socialmedia.data.entity.User
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import reactor.core.publisher.Mono

abstract class UserRepository {
    protected final ReactiveMongoTemplate mongoTemplate

    UserRepository(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate
    }

    protected Mono<User> findAndModifyByUserId(String userId, Update updateDefinition) {
        def query = findByIdQuery(userId)

        return mongoTemplate.findAndModify(
                query,
                updateDefinition,
                FindAndModifyOptions.options().returnNew(true),
                User,
                "users"
        )
    }

    protected Mono<Boolean> updateUser(String userId, Update update) {
        def query = findByIdQuery(userId)

        return mongoTemplate.updateFirst(query, update, User, "users")
                .map { it.modifiedCount > 0 }
    }

    protected Query findByIdQuery(String id) {
        return new Query(Criteria.where("_id").is(id))
    }

    protected Query findByUsernameQuery(String username) {
        return new Query(Criteria.where("username").is(username))
    }
}
