package com.kimlongdev.shopme_backend.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final String errorCode;

    // 400: Bad Request
    // 401: Unauthorized
    // 403: Forbidden
    // 404: Not Found
    // 500: Internal Server Error
    private final int statusCode;

    public BusinessException(String errorCode, String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }
}
