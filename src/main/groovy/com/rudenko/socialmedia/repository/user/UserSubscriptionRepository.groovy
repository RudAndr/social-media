package com.rudenko.socialmedia.repository.user

import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class UserSubscriptionRepository extends UserRepository {
    UserSubscriptionRepository(ReactiveMongoTemplate mongoTemplate) {
        super(mongoTemplate)
    }

    Mono<Boolean> subscribeToUser(String subjectId, String targetId) {
        def update = new Update().addToSet("subscriptions", targetId)

        return updateUser(subjectId, update)
    }

    Mono<Boolean> unsubscribeFromUser(String subjectId, String targetId) {
        def update = new Update().pull("subscriptions", targetId)

        return updateUser(subjectId, update)
    }
}
