package com.rudenko.socialmedia.controller.post

import com.rudenko.socialmedia.data.request.post.EditPostRequest
import com.rudenko.socialmedia.data.response.ApiResponse
import com.rudenko.socialmedia.data.response.PostResponse
import com.rudenko.socialmedia.data.entity.Post
import com.rudenko.socialmedia.data.entity.User
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
class PostController {

    private final PostGeneralService postService

    PostController(PostGeneralService postService) {
        this.postService = postService
    }

    @GetMapping(value = "/{username}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ApiResponse<PostResponse>> getPosts(@PathVariable(value = "username", required = false) String username,
                                             @AuthenticationPrincipal User currentUser) {
        return postService.getPosts(username, currentUser)
                .map { new PostResponse(it) }
                .map { ApiResponse.wrapOkResponse(it) }
    }

    @PostMapping
    Mono<ApiResponse<PostResponse>> createPost(@RequestBody PostResponse post) {
        return postService.createPost(new Post(post))
                .map { new PostResponse(it) }
                .map { ApiResponse.wrapOkResponse(it) }
    }

    @DeleteMapping("/{postId}")
    Mono<ApiResponse<Boolean>> deletePost(@PathVariable("postId") String postId) {
        return postService.deletePost(postId)
                .map { ApiResponse.wrapOkResponse(it) }
    }

    @PutMapping("/{postId}")
    Mono<ApiResponse<PostResponse>> editPost(@PathVariable("postId") String postId,
                                             @RequestBody EditPostRequest editPostRequest) {
        return postService.editPost(postId, editPostRequest)
                .map { new PostResponse(it) }
                .map { ApiResponse.wrapOkResponse(it) }
    }
}
