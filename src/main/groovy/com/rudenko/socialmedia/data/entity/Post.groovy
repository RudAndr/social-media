package com.rudenko.socialmedia.data.entity

import com.rudenko.socialmedia.data.response.PostResponse
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.FieldType

import java.time.LocalDateTime

@Document(value = "posts")
class Post {

    @Id
    @Field(targetType = FieldType.OBJECT_ID)
    private String id

    @Field("user_id")
    private String userId

    @Field("posted_at")
    private LocalDateTime postedAt

    @Field("message_text")
    private String messageText

    @Field("liked_by")
    private List<String> likedBy = []

    @Field("comments")
    private List<Comment> comments = []

    Post() {
    }

    Post(String id, String userId, LocalDateTime postedAt, String messageText, List<String> likedBy, List<Comment> comments) {
        this.id = id
        this.userId = userId
        this.postedAt = postedAt
        this.messageText = messageText
        this.likedBy = likedBy
        this.comments = comments
    }

    Post(PostResponse dto) {
        this.userId = dto.userId
        this.postedAt = dto.postedAt
        this.messageText = dto.messageText
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

    List<String> getLikedBy() {
        return likedBy
    }

    void setLikedBy(List<String> likedBy) {
        this.likedBy = likedBy
    }

    List<Comment> getComments() {
        return comments
    }

    void setComments(List<Comment> comments) {
        this.comments = comments
    }
}
