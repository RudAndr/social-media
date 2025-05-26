package com.rudenko.socialmedia.service

import com.rudenko.socialmedia.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class SubscriptionService {

    private final UserRepository userRepository

    SubscriptionService(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    Boolean subscribe(String subject, String target) {
        return userRepository.subscribeToUser(subject, target)
    }

    Boolean unsubscribe(String subject, String target) {
        return userRepository.unsubscribeFromUser(subject, target)
    }
}
