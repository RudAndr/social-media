package com.rudenko.socialmedia.data.response

import groovy.transform.CompileStatic
import org.springframework.http.HttpStatus

@CompileStatic
final class ApiResponse<T> {
    private final boolean success
    private final Integer status
    private final T data

    private ApiResponse(boolean success, Integer status, T data) {
        this.success = success
        this.status = status
        this.data = data
    }

    boolean getSuccess() {
        return Boolean.valueOf(success)
    }

    Integer getStatus() {
        return Integer.valueOf(status)
    }

    T getData() {
        return data
    }

    static <T> ApiResponse<T> wrapResponse(boolean success, Integer status, T data) {
        return new ApiResponse<T>(success, status, data)
    }

    static <T> ApiResponse<T> wrapOkResponse(T data) {
        return wrapResponse(true, HttpStatus.OK.value(), data)
    }
}
