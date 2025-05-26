package com.rudenko.socialmedia.data.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.rudenko.socialmedia.data.entity.Post

import java.time.LocalDateTime

@JsonInclude(Include.NON_NULL)
class PostDTO {
    private String id
    private String targetId
    private LocalDateTime postedAt
    private String messageText
    private Long likes

    PostDTO(Post post) {
        this.id = post.id
        this.targetId = post.targetId
        this.postedAt = post.postedAt
        this.messageText = post.messageText
    }

    String getId() {
        return id
    }

    void setId(String id) {
        this.id = id
    }

    String getTargetId() {
        return targetId
    }

    void setTargetId(String targetId) {
        this.targetId = targetId
    }

    LocalDateTime getPostedAt() {
        return postedAt
    }

    void setPostedAt(LocalDateTime postedAt) {
        this.postedAt = postedAt
    }

    String getMessageText() {
        return messageText
    }

    void setMessageText(String messageText) {
        this.messageText = messageText
    }

    Long getLikes() {
        return likes
    }

    void setLikes(Long likes) {
        this.likes = likes
    }
}
