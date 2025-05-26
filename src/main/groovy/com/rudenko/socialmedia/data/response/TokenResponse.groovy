package com.rudenko.socialmedia.data.response

class TokenResponse {
    private final String token

    TokenResponse(String token) {
        this.token = token
    }

    String getToken() {
        return token
    }
}