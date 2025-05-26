package com.rudenko.socialmedia.data.entity

import org.springframework.data.mongodb.core.mapping.Field

import java.time.LocalDateTime

class Comment {

    @Field(name = "id")
    private String id

    @Field("user_id")
    private String userId

    @Field("text")
    private String text

    @Field("posted_at")
    private LocalDateTime postedAt

    Comment() {
    }

    Comment(String id, String userId, LocalDateTime postedAt, String text) {
        this.id = id
        this.userId = userId
        this.text = text
        this.postedAt = postedAt
    }

    String getId() {
        return id
    }

    void setId(String id) {
        this.id = id
    }

    String getUserId() {
        return userId
    }

    void setUserId(String userId) {
        this.userId = userId
    }

    String getText() {
        return text
    }

    void setText(String text) {
        this.text = text
    }

    LocalDateTime getPostedAt() {
        return postedAt
    }

    void setPostedAt(LocalDateTime postedAt) {
        this.postedAt = postedAt
    }
}
