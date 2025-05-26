package com.rudenko.socialmedia.repository.user

import com.rudenko.socialmedia.data.entity.User
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class UserInfoRepository extends UserRepository {

    UserInfoRepository(ReactiveMongoTemplate mongoTemplate) {
        super(mongoTemplate)
    }

    Mono<User> findById(String userId) {
        def query = findByIdQuery(userId)

        return mongoTemplate.findOne(query, User, "users")
    }

    Mono<User> findByUsername(String username) {
        def query = findByUsernameQuery(username)

        return mongoTemplate.findOne(query, User, "users")
    }

    Mono<User> save(User user) {
        return mongoTemplate.save(user, "users")
    }

    Mono<Boolean> deleteByUserId(String userId) {
        def query = findByIdQuery(userId)

        return mongoTemplate.remove(query, "users")
                .map { it.deletedCount > 0 }
    }

    Mono<User> changeUsername(String userId, String value) {
        def update = new Update().set("username", value)

        return findAndModifyByUserId(userId, update)
    }

    Mono<User> changePassword(String userId, String value) {
        def update = new Update().set("password", value)

        return findAndModifyByUserId(userId, update)
    }

    Mono<User> changeDisplayName(String userId, String value) {
        def update = new Update().set("displayName", value)

        return findAndModifyByUserId(userId, update)
    }
}
