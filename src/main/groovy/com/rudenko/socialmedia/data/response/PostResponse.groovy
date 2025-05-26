package com.rudenko.socialmedia.data.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty
import com.rudenko.socialmedia.data.entity.Comment
import com.rudenko.socialmedia.data.entity.Post

import java.time.LocalDateTime

@JsonInclude(Include.NON_NULL)
class PostResponse {
    private String id
    private String userId
    private LocalDateTime postedAt
    private String messageText
    private List<String> likes
    private List<Comment> comments

    PostResponse() {
    }

    @JsonCreator
    PostResponse(@JsonProperty("id") String id,
                 @JsonProperty("userId") String userId,
                 @JsonProperty("postedAt") LocalDateTime postedAt,
                 @JsonProperty("messageText") String messageText,
                 @JsonProperty("likes") List<String> likes,
                 @JsonProperty("comments") List<Comment> comments) {
        this.id = id
        this.userId = userId
        this.postedAt = postedAt
        this.messageText = messageText
        this.likes = likes
        this.comments = comments
    }

    PostResponse(Post post) {
        this.id = post.id
        this.userId = post.userId
        this.postedAt = post.postedAt
        this.messageText = post.messageText
        this.likes = post.likedBy
        this.comments = post.comments
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

    void setUserId(String targetId) {
        this.userId = targetId
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

    List<String> getLikes() {
        return likes
    }

    void setLikes(List<String> likes) {
        this.likes = likes
    }

    List<Comment> getComments() {
        return comments
    }

    void setComments(List<Comment> comments) {
        this.comments = comments
    }
}
