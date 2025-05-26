package com.rudenko.socialmedia.controller.post

import com.rudenko.socialmedia.data.entity.User
import com.rudenko.socialmedia.data.request.post.EditPostRequest
import com.rudenko.socialmedia.data.request.post.PostEditAction
import com.rudenko.socialmedia.data.response.ApiResponse
import com.rudenko.socialmedia.data.response.CommentResponse
import com.rudenko.socialmedia.data.response.PostResponse
import com.rudenko.socialmedia.service.post.PostService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/posts/{action}")
class PostActionController {

    private final PostService postService

    PostActionController(PostService postService) {
        this.postService = postService
    }

    Mono<ApiResponse<PostResponse>> likePost() {

    }

    Mono<ApiResponse<PostResponse>> dislikePost() {
        
    }

    @PutMapping("/{action}/{postId}") //todo refactor
    Mono<ApiResponse<PostResponse>> editPost(@PathVariable("action") PostEditAction postAction,
                                             @PathVariable("postId") String postId,
                                             @RequestBody(required = false) EditPostRequest editPostRequest,
                                             @AuthenticationPrincipal User currentUser) {
        return postService.editPost(postAction, postId, editPostRequest, currentUser)
                .map { new PostResponse(it) }
                .map { ApiResponse.wrapOkResponse(it) }
    }



    @PostMapping("/{postId}/comments")
    Mono<ApiResponse<CommentResponse>> postComment(@PathVariable String postId) {

    }


}
