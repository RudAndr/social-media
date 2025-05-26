package com.rudenko.socialmedia.repository

import com.rudenko.socialmedia.data.entity.User
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
class UserRepository {

    private final MongoTemplate mongoTemplate

    UserRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate
    }

    User findByUsername(String username) {
        def query = findByIdQuery(username)

        return mongoTemplate.findOne(query, User, "users")
    }

    User save(User user) {
        return mongoTemplate.save(user, "users")
    }

    Boolean deleteByUsername(String username) {
        def query = findByIdQuery(username)

        return mongoTemplate.remove(query, "users")
    }

    Boolean subscribeToUser(String subject, String target) {
        def update = new Update().addToSet("subscriptions", target)

        return updateUser(subject, update)
    }

    Boolean unsubscribeFromUser(String subject, String target) {
        def update = new Update().pull("subscriptions", target)

        return updateUser(subject, update)
    }

    private Boolean updateUser(String userId, Update update) {
        def query = findByIdQuery(userId)

        return mongoTemplate.updateFirst(query, update, User, "users")
    }

    private Query findByIdQuery(String username) {
        return new Query(Criteria.where("username").is(username))
    }
}
