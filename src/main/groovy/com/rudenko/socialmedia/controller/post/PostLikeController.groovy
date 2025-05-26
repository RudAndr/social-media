package com.rudenko.socialmedia.controller.post

import com.rudenko.socialmedia.data.response.ApiResponse
import com.rudenko.socialmedia.data.response.PostResponse
import com.rudenko.socialmedia.data.security.SocialUserDetails
import com.rudenko.socialmedia.service.post.PostLikeService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/posts/{postId}/like")
class PostLikeController {

    private final PostLikeService postLikeService

    PostLikeController(PostLikeService postLikeService) {
        this.postLikeService = postLikeService
    }

    @PostMapping
    Mono<ApiResponse<PostResponse>> addLike(@PathVariable("postId") String postId,
                                            @AuthenticationPrincipal SocialUserDetails currentUser) {
        return postLikeService.addLike(postId, currentUser.user)
                .map { new PostResponse(it) }
                .map { ApiResponse.wrapOkResponse(it) }
    }

    @DeleteMapping
    Mono<ApiResponse<PostResponse>> removeLike(@PathVariable("postId") String postId,
                                               @AuthenticationPrincipal SocialUserDetails currentUser) {
        return postLikeService.removeLike(postId, currentUser.user)
                .map { new PostResponse(it) }
                .map { ApiResponse.wrapOkResponse(it) }
    }
}
