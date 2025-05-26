package com.rudenko.socialmedia.service.user

import com.rudenko.socialmedia.data.entity.User
import com.rudenko.socialmedia.data.exception.EditNotAllowedException
import com.rudenko.socialmedia.data.request.user.UserEditRequest
import com.rudenko.socialmedia.repository.user.UserInfoRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

import static com.rudenko.socialmedia.data.request.user.UserEditAction.DISPLAY_NAME_CHANGE
import static com.rudenko.socialmedia.data.request.user.UserEditAction.PASSWORD_CHANGE
import static com.rudenko.socialmedia.data.request.user.UserEditAction.USERNAME_CHANGE

@Service
class UserInfoService {
    def userEditActionMap = [
            (USERNAME_CHANGE)     : changeUsernameAction(),
            (PASSWORD_CHANGE)     : passwordChangeAction(),
            (DISPLAY_NAME_CHANGE) : displayNameChangeAction()
    ]

    private final UserInfoRepository userInfoRepository
    private final PasswordEncoder passwordEncoder

    UserInfoService(UserInfoRepository userInfoRepository,
                    PasswordEncoder passwordEncoder) {
        this.userInfoRepository = userInfoRepository
        this.passwordEncoder = passwordEncoder
    }

    Mono<User> editUser(String userId, UserEditRequest editRequest, User currentUser) {
        return isAllowedEditing(userId, currentUser)
                .filter { it }
                .flatMap { editUserWithId(userId, editRequest)  }
                .switchIfEmpty { Mono.error(new EditNotAllowedException("Editing different users is forbidden!")) }
    }

    Mono<User> findUserById(String userId) {
        return userInfoRepository.findById(userId)
    }

    Mono<User> findUserByUsername(String username) {
        return userInfoRepository.findByUsername(username)
    }

    Mono<Boolean> deleteUser(String userId, User currentUser) {
        return isAllowedEditing(userId, currentUser)
                .filter { it }
                .flatMap { userInfoRepository.deleteByUserId(userId)  }
                .switchIfEmpty { Mono.error(new EditNotAllowedException("Deleting different users is forbidden!")) }


    }

    Mono<User> createUser(User user) {
        return userInfoRepository.save(user.tap { it.password = passwordEncoder.encode(it.password) })
    }

    private Mono<User> editUserWithId(String userId, UserEditRequest editRequest) {
        return userEditActionMap[editRequest.getAction()](userId, editRequest.newValue)
    }

    private Closure<Mono<User>> changeUsernameAction() {
        return { String userId, String value ->
            return userInfoRepository.changeUsername(userId, value)
        }
    }

    private Closure<Mono<User>> passwordChangeAction() {
        return { String userId, String value ->
            def encryptedPassword = passwordEncoder.encode(value)

            return userInfoRepository.changePassword(userId, encryptedPassword)
        }
    }

    private Closure<Mono<User>> displayNameChangeAction() {
        return { String userId, String value ->
            return userInfoRepository.changeDisplayName(userId, value)
        }
    }

    private Mono<Boolean> isAllowedEditing(String userId, User currentUser) {
        return Mono.just(currentUser.id == userId)
    }
}
