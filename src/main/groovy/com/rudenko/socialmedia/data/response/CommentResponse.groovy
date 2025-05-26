package com.rudenko.socialmedia.data.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.rudenko.socialmedia.data.entity.Comment

class CommentResponse {
    private String postId
    private Comment comment

    @JsonCreator
    CommentResponse(@JsonProperty("postId") String postId,
                    @JsonProperty("comment") Comment comment) {
        this.postId = postId
        this.comment = comment
    }

    String getPostId() {
        return postId
    }

    void setPostId(String postId) {
        this.postId = postId
    }

    Comment getComment() {
        return comment
    }

    void setComment(Comment comment) {
        this.comment = comment
    }
}
