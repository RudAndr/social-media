package com.rudenko.socialmedia.controller.user

import com.rudenko.socialmedia.data.response.ApiResponse
import com.rudenko.socialmedia.data.security.SocialUserDetails
import com.rudenko.socialmedia.service.user.UserSubscriptionService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/users/{userId}/subscribe")
class UserSubscriptionController {
    private final UserSubscriptionService subscriptionService

    UserSubscriptionController(UserSubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService
    }

    @PostMapping
    Mono<ApiResponse<Boolean>> subscribe(@PathVariable("userId") String subscribeTo,
                                         @AuthenticationPrincipal SocialUserDetails currentUser) {
        return subscriptionService.subscribe(currentUser.user.id, subscribeTo)
                .map { ApiResponse.wrapOkResponse(it) }
    }

    @DeleteMapping
    Mono<ApiResponse<Boolean>> unsubscribe(@PathVariable("userId") String unsubscribeFrom,
                                           @AuthenticationPrincipal SocialUserDetails currentUser) {
        return subscriptionService.unsubscribe(currentUser.user.id, unsubscribeFrom)
                .map { ApiResponse.wrapOkResponse(it) }
    }
}
