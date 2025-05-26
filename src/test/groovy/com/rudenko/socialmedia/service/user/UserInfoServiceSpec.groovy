package com.rudenko.socialmedia.service.user

import com.rudenko.socialmedia.data.entity.User
import com.rudenko.socialmedia.data.exception.EditNotAllowedException
import com.rudenko.socialmedia.data.request.user.UserEditAction
import com.rudenko.socialmedia.data.request.user.UserEditRequest
import com.rudenko.socialmedia.repository.user.UserInfoRepository
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification
import spock.lang.Subject

class UserInfoServiceSpec extends Specification {

    UserInfoRepository userInfoRepository = Mock()
    PasswordEncoder passwordEncoder = Mock()

    @Subject
    UserInfoService userInfoService

    def setup() {
        userInfoService = new UserInfoService(userInfoRepository, passwordEncoder)
    }

    def "should find user by id"() {
        given: "we have userId"
        def userId = "user123"

        when: "find user by id method is called"
        def result = userInfoService.findUserById(userId)

        and: "executed"
        StepVerifier
                .create(result)
                .expectNextMatches {
                    it.id == userId
                    it.username == "testuser"
                }
                .verifyComplete()


        then: "we expect 1 repository call"
        1 * userInfoRepository.findById(userId) >> Mono.just(new User(id: userId, username: "testuser"))
    }

    def "should find user by username"() {
        given: "we have username"
        def username = "testuser"

        when: "find user by username method is called"
        def result = userInfoService.findUserByUsername(username)

        and: "executed"
        StepVerifier
                .create(result)
                .expectNextMatches {
                    it.id == "user123"
                    it.username == username
                }
                .verifyComplete()

        then: "we expect 1 repository call"
        1 * userInfoRepository.findByUsername(username) >> Mono.just(new User(id: "user123", username: username))
    }

    def "should delete user"() {
        given: "we have username to delete"
        def userId = "testuser"
        def currentUser = new User(id: userId)

        when: "delete user method is called"
        def result = userInfoService.deleteUser(userId, currentUser)

        and: "executed"
        StepVerifier
                .create(result)
                .expectNextMatches {
                    it == true
                }
                .verifyComplete()

        then: "we expect 1 repository call"
        1 * userInfoRepository.deleteByUserId(userId) >> Mono.just(true)
    }

    def "should create user with encoded password"() {
        given: "we have a user with password"
        def user = new User(id: "user123", username: "testuser", password: "password123")
        def encodedPassword = "encoded_password"

        when: "create user method is called"
        def result = userInfoService.createUser(user)

        and: "executed"
        StepVerifier
                .create(result)
                .expectNextMatches {
                    it.id == "user123"
                    it.username == "testuser"
                    it.password == encodedPassword
                }
                .verifyComplete()

        then: "we expect password encoding and repository call"
        1 * passwordEncoder.encode("password123") >> encodedPassword
        1 * userInfoRepository.save({ User u -> 
            u.id == "user123" && 
            u.username == "testuser" && 
            u.password == encodedPassword 
        }) >> { User u -> Mono.just(u) }
    }

    def "should edit username when allowed"() {
        given: "we have a user who wants to change their username"
        def userId = "user123"
        def currentUser = new User(id: userId)
        def newUsername = "newusername"
        def editRequest = new UserEditRequest(UserEditAction.USERNAME_CHANGE, newUsername)

        when: "edit user method is called"
        def result = userInfoService.editUser(userId, editRequest, currentUser)

        and: "executed"
        StepVerifier
                .create(result)
                .expectNextMatches {
                    it.id == userId
                    it.username == newUsername
                }
                .verifyComplete()

        then: "we expect 1 repository call to change username"
        1 * userInfoRepository.changeUsername(userId, newUsername) >> Mono.just(new User(id: userId, username: newUsername))
    }

    def "should edit password when allowed"() {
        given: "we have a user who wants to change their password"
        def userId = "user123"
        def currentUser = new User(id: userId)
        def newPassword = "newpassword"
        def encodedPassword = "encoded_newpassword"
        def editRequest = new UserEditRequest(UserEditAction.PASSWORD_CHANGE, newPassword)

        when: "edit user method is called"
        def result = userInfoService.editUser(userId, editRequest, currentUser)

        and: "executed"
        StepVerifier
                .create(result)
                .expectNextMatches {
                    it.id == userId
                    it.password == encodedPassword
                }
                .verifyComplete()

        then: "we expect password encoding and repository call"
        1 * passwordEncoder.encode(newPassword) >> encodedPassword
        1 * userInfoRepository.changePassword(userId, encodedPassword) >> Mono.just(new User(id: userId, password: encodedPassword))
    }

    def "should edit display name when allowed"() {
        given: "we have a user who wants to change their display name"
        def userId = "user123"
        def currentUser = new User(id: userId)
        def newDisplayName = "New Display Name"
        def editRequest = new UserEditRequest(UserEditAction.DISPLAY_NAME_CHANGE, newDisplayName)

        when: "edit user method is called"
        def result = userInfoService.editUser(userId, editRequest, currentUser)

        and: "executed"
        StepVerifier
                .create(result)
                .expectNextMatches {
                    it.id == userId
                    it.displayName == newDisplayName
                }
                .verifyComplete()

        then: "we expect 1 repository call to change display name"
        1 * userInfoRepository.changeDisplayName(userId, newDisplayName) >> Mono.just(new User(id: userId, displayName: newDisplayName))
    }

    def "should throw exception when editing different user"() {
        given: "we have a user trying to edit another user's profile"
        def userId = "user123"
        def currentUser = new User(id: "different_user")
        def editRequest = new UserEditRequest(UserEditAction.USERNAME_CHANGE, "newusername")

        when: "edit user method is called"
        def result = userInfoService.editUser(userId, editRequest, currentUser)

        and: "executed"
        StepVerifier
                .create(result)
                .expectError(EditNotAllowedException)

        then: "we expect no repository calls"
        0 * userInfoRepository.changeUsername(_, _)
    }
}
