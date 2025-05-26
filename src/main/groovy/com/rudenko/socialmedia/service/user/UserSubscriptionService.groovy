package com.rudenko.socialmedia.service.user

import com.rudenko.socialmedia.repository.user.UserSubscriptionRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserSubscriptionService {

    private final UserSubscriptionRepository userSubscriptionRepository

    UserSubscriptionService(UserSubscriptionRepository userSubscriptionRepository) {
        this.userSubscriptionRepository = userSubscriptionRepository
    }

    Mono<Boolean> subscribe(String subjectId, String targetId) {
        return userSubscriptionRepository.subscribeToUser(subjectId, targetId)
    }

    Mono<Boolean> unsubscribe(String subjectId, String targetId) {
        return userSubscriptionRepository.unsubscribeFromUser(subjectId, targetId)
    }
}
