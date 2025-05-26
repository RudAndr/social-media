package com.rudenko.socialmedia.controller.user

import com.rudenko.socialmedia.data.entity.User
import com.rudenko.socialmedia.data.request.user.UserEditRequest
import com.rudenko.socialmedia.data.response.ApiResponse
import com.rudenko.socialmedia.data.response.UserResponse
import com.rudenko.socialmedia.data.security.SocialUserDetails
import com.rudenko.socialmedia.service.user.UserInfoService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/users/{userId}/info")
class UserInfoController {
    private final UserInfoService userService

    UserInfoController(UserInfoService userService) {
        this.userService = userService
    }

    @GetMapping
    Mono<ApiResponse<UserResponse>> getUser(@PathVariable("userId") String userId) {
        return userService.findUserById(userId)
                .map {new UserResponse(it.id, it.username, it.displayName) }
                .map { ApiResponse.wrapOkResponse(it) }
    }

    @PutMapping
    Mono<ApiResponse<UserResponse>> editUser(@PathVariable("userId") String userId,
                                             @RequestBody UserEditRequest editRequest,
                                             @AuthenticationPrincipal SocialUserDetails currentUser) {
        return userService.editUser(userId, editRequest, currentUser.user)
                .map { new UserResponse(it.id, it.username, it.displayName) }
                .map { ApiResponse.wrapOkResponse(it) }
    }

    @DeleteMapping
    Mono<ApiResponse<Boolean>> deleteUser(@PathVariable("userId") String userId,
                                          @AuthenticationPrincipal SocialUserDetails currentUser) {
        return userService.deleteUser(userId, currentUser.user)
                .map { ApiResponse.wrapOkResponse(it) }
    }
}
