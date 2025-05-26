package com.rudenko.socialmedia.controller.post

import com.rudenko.socialmedia.data.request.post.EditPostRequest
import com.rudenko.socialmedia.data.response.ApiResponse
import com.rudenko.socialmedia.data.response.PostResponse
import com.rudenko.socialmedia.data.entity.Post
import com.rudenko.socialmedia.data.entity.User
import com.rudenko.socialmedia.data.security.SocialUserDetails
import com.rudenko.socialmedia.service.post.PostGeneralService
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/api/v1/posts")
class PostGeneralController {

    private final PostGeneralService postService

    PostGeneralController(PostGeneralService postService) {
        this.postService = postService
    }

    @GetMapping("/{userId}")
    Flux<ApiResponse<PostResponse>> getPosts(@PathVariable(value = "userId", required = false) String userId,
                                             @AuthenticationPrincipal SocialUserDetails currentUser) {
        return postService.getPosts(userId, currentUser.user)
                .map { new PostResponse(it) }
                .map { ApiResponse.wrapOkResponse(it) }
    }

    @PostMapping
    Mono<ApiResponse<PostResponse>> createPost(@RequestBody PostResponse post,
                                               @AuthenticationPrincipal SocialUserDetails currentUser) {
        return postService.createPost(new Post(post).tap { it.userId = currentUser.user.id })
                .map { new PostResponse(it) }
                .map { ApiResponse.wrapOkResponse(it) }
    }

    @DeleteMapping("/{postId}")
    Mono<ApiResponse<Boolean>> deletePost(@PathVariable("postId") String postId,
                                          @AuthenticationPrincipal SocialUserDetails currentUser) {
        return postService.deletePost(postId, currentUser.user.id)
                .map { ApiResponse.wrapOkResponse(it) }
    }

    @PutMapping("/{postId}")
    Mono<ApiResponse<PostResponse>> editPost(@PathVariable("postId") String postId,
                                             @RequestBody EditPostRequest editPostRequest,
                                             @AuthenticationPrincipal SocialUserDetails currentUser) {
        return postService.editPost(postId, editPostRequest, currentUser.user.id)
                .map { new PostResponse(it) }
                .map { ApiResponse.wrapOkResponse(it) }
    }
}
