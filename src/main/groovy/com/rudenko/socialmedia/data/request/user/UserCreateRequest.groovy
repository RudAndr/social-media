package com.rudenko.socialmedia.data.request.user

class UserCreateRequest {
    private String username
    private String password
    private String displayName

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

    String getDisplayName() {
        return displayName
    }

    void setDisplayName(String displayName) {
        this.displayName = displayName
    }
}
