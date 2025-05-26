package com.rudenko.socialmedia.service.post


import com.rudenko.socialmedia.data.response.CommentResponse
import com.rudenko.socialmedia.data.response.CommentsResponse
import com.rudenko.socialmedia.repository.post.PostCommentRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

import static com.rudenko.socialmedia.data.request.post.PostEditAction.COMMENT_ADD
import static com.rudenko.socialmedia.data.request.post.PostEditAction.COMMENT_REMOVE

@Service
class PostCommentService {

    private def final commentHandlers = [
            (COMMENT_ADD)   : addCommentAction(),
            (COMMENT_REMOVE): removeCommentAction()
    ]

    private final PostCommentRepository postCommentRepository

    PostCommentService(PostCommentRepository postCommentRepository) {
        this.postCommentRepository = postCommentRepository
    }

    Flux<CommentResponse> getComments(String postId) {
        return postCommentRepository.getComments(postId)
                .map { new CommentResponse(postId, it) }
    }

    Mono<CommentsResponse> createComment(String postId, String userId, String text) {
        return commentHandlers[COMMENT_ADD](postId, userId, text)
    }

    Mono<CommentsResponse> deleteComment(String postId, String commentId, String userId) {
        return commentHandlers[COMMENT_REMOVE](postId, commentId, userId)
    }

    private Closure<Mono<CommentsResponse>> addCommentAction() {
        return { String postId, String userId, String text ->
            postCommentRepository.addComment(postId, userId, text)
        }
    }

    private Closure<Mono<CommentsResponse>> removeCommentAction() {
        return { String postId, String commentId, String userId ->
            postCommentRepository.removeComment(commentId, userId, postId)
        }
    }
}
