package com.kimlongdev.shopme_backend.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final String errorCode; // Ví dụ: RESOURCE_NOT_FOUND
    private final int statusCode;   // Ví dụ: 404, 400, 500

    public ApiException(String errorCode, String message, int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }
}
