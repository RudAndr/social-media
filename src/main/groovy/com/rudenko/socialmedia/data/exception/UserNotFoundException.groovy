package com.rudenko.socialmedia.data.exception

class UserNotFoundException extends RuntimeException {
    UserNotFoundException(String message) {
        super(message)
    }
}
