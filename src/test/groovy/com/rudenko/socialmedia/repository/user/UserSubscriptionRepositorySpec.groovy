package com.rudenko.socialmedia.repository.user

import com.rudenko.socialmedia.data.entity.User
import com.rudenko.socialmedia.repository.SpockDbIntegrationTest
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier

class UserSubscriptionRepositorySpec extends SpockDbIntegrationTest {

    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository

    @Autowired
    private UserInfoRepository userInfoRepository

    def setup() {
        mongoContainer.execInContainer("mongo", "--eval", "rs.initiate()")
    }

    def "should subscribe to user"() {
        given: "we have 2 users"
        def subjectUser = userInfoRepository.save(new User(username: "subject_user")).block()
        def targetUser = userInfoRepository.save(new User(username: "target_user")).block()

        when: "subscribe method is called"
        def result = userSubscriptionRepository.subscribeToUser(subjectUser.id, targetUser.id)

        and: "executed"
        StepVerifier.create(result)
                .expectNextMatches {
                    it == true
                }.verifyComplete()

        then:
        "User subscribed"

        and: "one user contains subscription to another"
        def updatedUser = userInfoRepository.findById(subjectUser.id).block()
        updatedUser.subscriptions.contains(targetUser.id)
    }

    def "should unsubscribe from user"() {
        given:
        def subjectUser = userInfoRepository.save(new User(username: "subject_user_2")).block()
        def targetUser = userInfoRepository.save(new User(username: "target_user_2")).block()

        userSubscriptionRepository.subscribeToUser(subjectUser.id, targetUser.id).block()

        when:
        def result = userSubscriptionRepository.unsubscribeFromUser(subjectUser.id, targetUser.id)

        and:
        StepVerifier.create(result)
                .expectNextMatches {
                    it == true
                }.verifyComplete()

        then:
        "User unsubscribed"

        and: "one user does not contains subscription to another"
        def updatedUser = userInfoRepository.findById(subjectUser.id).block()
        !updatedUser.subscriptions.contains(targetUser.id)
    }
}
