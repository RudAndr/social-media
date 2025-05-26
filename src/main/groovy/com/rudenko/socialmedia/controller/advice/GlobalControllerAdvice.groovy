package com.rudenko.socialmedia.controller.advice

import com.rudenko.socialmedia.data.exception.InvalidCredentialsException
import com.rudenko.socialmedia.data.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestControllerAdvice
class GlobalControllerAdvice {

    @ExceptionHandler(InvalidCredentialsException.class)
    Mono<ApiResponse<String>> handleRuntimeException(InvalidCredentialsException exception, ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON)

        return Mono.just(ApiResponse.wrapResponse(false, HttpStatus.UNAUTHORIZED.value(), exception.message))
    }
}
