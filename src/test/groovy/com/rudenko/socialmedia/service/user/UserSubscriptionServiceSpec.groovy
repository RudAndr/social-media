package com.rudenko.socialmedia.service.user

import com.rudenko.socialmedia.repository.user.UserSubscriptionRepository
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification
import spock.lang.Subject

class UserSubscriptionServiceSpec extends Specification {

    UserSubscriptionRepository userSubscriptionRepository = Mock()

    @Subject
    UserSubscriptionService userSubscriptionService

    def setup() {
        userSubscriptionService = new UserSubscriptionService(userSubscriptionRepository)
    }

    def "should subscribe user successfully"() {
        given: "we have a user who wants to subscribe to another user"
        def subjectId = "user123"
        def targetId = "user456"

        when: "subscribe method is called"
        def result = userSubscriptionService.subscribe(subjectId, targetId)

        and: "executed"
        StepVerifier
                .create(result)
                .expectNextMatches {
                    it == true
                }
                .verifyComplete()

        then: "we expect 1 repository call"
        1 * userSubscriptionRepository.subscribeToUser(subjectId, targetId) >> Mono.just(true)
    }

    def "should unsubscribe user successfully"() {
        given: "we have a user who wants to unsubscribe from another user"
        def subjectId = "user123"
        def targetId = "user456"

        when: "unsubscribe method is called"
        def result = userSubscriptionService.unsubscribe(subjectId, targetId)

        and: "executed"
        StepVerifier
                .create(result)
                .expectNextMatches {
                    it == true
                }
                .verifyComplete()
        then: "we expect 1 repository call"
        1 * userSubscriptionRepository.unsubscribeFromUser(subjectId, targetId) >> Mono.just(true)
    }
}
