package com.rudenko.socialmedia.repository.user

import com.rudenko.socialmedia.data.entity.User
import com.rudenko.socialmedia.repository.SpockDbIntegrationTest
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier

class UserInfoRepositorySpec extends SpockDbIntegrationTest {

    @Autowired
    private UserInfoRepository userInfoRepository

    def setup() {
        mongoContainer.execInContainer("mongo", "--eval", "rs.initiate()")
    }

    def "should save user"() {
        given: "user for saving"
        def user = new User(username: "test_user")

        when: "repository method called"
        def result = userInfoRepository.save(user)

        and: "executed"
        StepVerifier.create(result)
                .expectNextMatches {
                    it.id != null
                    it.username == "test_user"
                }.verifyComplete()

        then:
        "User saved"
    }

    def "should find user by id"() {
        given: "we have saved user"
        def savedUser = userInfoRepository.save(new User(username: "find_by_id_user")).block()

        when: "find method called"
        def result = userInfoRepository.findById(savedUser.id)

        and: "executed correctly"
        StepVerifier.create(result)
                .expectNextMatches {
                    it.id == savedUser.id
                    it.username == "find_by_id_user"
                }.verifyComplete()

        then:
        "User found by id"
    }

    def "should find user by username"() {
        given: "we have saved user with username"
        def username = "find_by_username_user"
        userInfoRepository.save(new User(username: username)).block()

        when: "findByUsername method is called"
        def result = userInfoRepository.findByUsername(username)

        and: "executed"
        StepVerifier.create(result)
                .expectNextMatches {
                    it.id != null
                    it.username == username
                }.verifyComplete()

        then:
        "User found by username"
    }

    def "should delete user by user id"() {
        given: "we have saved user we want to delete"
        def user = userInfoRepository.save(new User(username: "delete_user")).block()

        when: "delete method is called"
        def result = userInfoRepository.deleteByUserId(user.id)

        and: "executed"
        StepVerifier.create(result)
                .expectNextMatches {
                    it == true
                }.verifyComplete()

        then:
        "User deleted"

        and: "absent in the database"
        StepVerifier.create(userInfoRepository.findByUsername("delete_user"))
                .expectNextCount(0)
                .verifyComplete()
    }

    def "should change username"() {
        given: "we have saved user and we want to change username"
        def user = userInfoRepository.save(new User(username: "old_username")).block()
        def newUsername = "new_username"

        when: "change method is called"
        def result = userInfoRepository.changeUsername(user.id, newUsername)

        and: "executed"
        StepVerifier.create(result)
                .expectNextMatches {
                    it.id == user.id
                    it.username == newUsername
                }.verifyComplete()

        then:
        "Username changed"
    }

    def "should change password"() {
        given: "we have saved user and we want to change password"
        def user = userInfoRepository.save(new User(username: "password_user", password: "old_password")).block()
        def newPassword = "new_password"

        when: "change method is called"
        def result = userInfoRepository.changePassword(user.id, newPassword)

        and: "executed"
        StepVerifier.create(result)
                .expectNextMatches {
                    it.id == user.id
                    it.password == newPassword
                }.verifyComplete()

        then:
        "Password changed"
    }

    def "should change display name"() {
        given: "we have saved user and we want to change displayName"
        def user = userInfoRepository.save(new User(username: "display_name_user")).block()
        def newDisplayName = "New Display Name"

        when: "change method is called"
        def result = userInfoRepository.changeDisplayName(user.id, newDisplayName)

        and: "executed"
        StepVerifier.create(result)
                .expectNextMatches {
                    it.id == user.id
                    it.displayName == newDisplayName
                }.verifyComplete()

        then:
        "Display name changed"
    }
}
