package com.rudenko.socialmedia.data.response

import com.rudenko.socialmedia.data.entity.Comment

class CommentsResponse {
    private String postId
    private List<Comment> comments

    CommentsResponse() {}

    CommentsResponse(String postId, List<Comment> comments) {
        this.postId = postId
        this.comments = comments
    }

    String getPostId() {
        return postId
    }

    void setPostId(String postId) {
        this.postId = postId
    }

    List<Comment> getComments() {
        return comments
    }

    void setComments(List<Comment> comments) {
        this.comments = comments
    }
}
