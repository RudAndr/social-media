package com.rudenko.socialmedia.data.request.user

class LoginRequest {
    private String username
    private String password

    String getUsername() {
        return username
    }

    void setUsername(String username) {
        this.username = username
    }

    String getPassword() {
        return password
    }

    void setPassword(String password) {
        this.password = password
    }
}
