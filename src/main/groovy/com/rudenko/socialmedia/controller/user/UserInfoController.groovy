package com.rudenko.socialmedia.controller

import com.rudenko.socialmedia.data.dto.ApiResponse
import com.rudenko.socialmedia.data.dto.UserDTO
import com.rudenko.socialmedia.data.entity.User
import com.rudenko.socialmedia.service.user.UserSubscriptionService
import com.rudenko.socialmedia.service.user.UserInfoService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController {
    private final UserInfoService userService
    private final UserSubscriptionService subscriptionService

    UserController(UserSubscriptionService subscriptionService,
                   UserInfoService userService) {
        this.userService = userService
        this.subscriptionService = subscriptionService
    }

    @GetMapping("/{username}")
    ApiResponse<UserDTO> getUser(@PathVariable("username") String username) {
        return userService.findUser(username)
                ?.with {new UserDTO(it.username, it.displayName) }
                ?.with { ApiResponse.wrapOkResponse(it) }
    }

    @DeleteMapping("/{username}")
    ApiResponse<Boolean> deleteUser(@PathVariable("username") String username) {
        return userService.deleteUser(username)
                .with { ApiResponse.wrapOkResponse(it) }
    }

    @PostMapping("/subscribe/{subscribeTo}")
    ApiResponse<Boolean> subscribe(@PathVariable("subscribeTo") String username,
                                   @AuthenticationPrincipal User currentUser) {
        return subscriptionService.subscribe(username, currentUser.username)
                .with { ApiResponse.wrapOkResponse(it) }
    }

    @DeleteMapping("/unsubscribe/{unsubscribeFrom}")
    ApiResponse<Boolean> unsubscribe(@PathVariable("unsubscribeFrom") String username,
                                     @AuthenticationPrincipal User currentUser) {
        return subscriptionService.unsubscribe(username, currentUser.username)
                .with { ApiResponse.wrapOkResponse(it) }
    }
}
