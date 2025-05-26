package com.rudenko.socialmedia.service

import com.rudenko.socialmedia.data.entity.User
import com.rudenko.socialmedia.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService {

    private final UserRepository userRepository

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    User findUser(String username) {
        return userRepository.findByUsername(username)
    }

    Boolean deleteUser(String username) {
        userRepository.deleteByUsername(username)
    }

    User createUser(User user) {
        return userRepository.save(user)
    }
}
