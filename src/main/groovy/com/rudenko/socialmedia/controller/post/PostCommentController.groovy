package com.rudenko.socialmedia.controller.post

import com.rudenko.socialmedia.data.entity.User
import com.rudenko.socialmedia.data.request.post.EditPostRequest
import com.rudenko.socialmedia.data.response.ApiResponse
import com.rudenko.socialmedia.data.response.CommentResponse
import com.rudenko.socialmedia.data.response.CommentsResponse
import com.rudenko.socialmedia.data.security.SocialUserDetails
import com.rudenko.socialmedia.service.post.PostCommentService
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
class PostCommentController {

    private final PostCommentService postCommentService

    PostCommentController(PostCommentService postCommentService) {
        this.postCommentService = postCommentService
    }

    @PostMapping
    Mono<ApiResponse<CommentsResponse>> postComment(@PathVariable("postId") String postId,
                                                    @RequestBody EditPostRequest editPostRequest,
                                                    @AuthenticationPrincipal SocialUserDetails currentUser) {
        return postCommentService.createComment(postId, currentUser.user.id, editPostRequest.text)
                .map { ApiResponse.wrapOkResponse(it) }
    }

    @DeleteMapping("/{commentId}")
    Mono<ApiResponse<CommentsResponse>> deleteComment(@PathVariable("postId") String postId,
                                                     @PathVariable("commentId") String commentId,
                                                     @AuthenticationPrincipal SocialUserDetails currentUser) {
        return postCommentService.deleteComment(postId, commentId, currentUser.user.id)
                .map { ApiResponse.wrapOkResponse(it) }
    }

    @GetMapping
    Flux<ApiResponse<CommentResponse>> getComments(@PathVariable("postId") String postId) {
        return postCommentService.getComments(postId)
                .map { ApiResponse.wrapOkResponse(it) }
    }
}
