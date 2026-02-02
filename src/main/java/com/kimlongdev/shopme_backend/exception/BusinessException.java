package com.kimlongdev.shopme_backend.exception;

public class BusinessException extends Exception {
    public BusinessException(Object message) {
        super((String) message);
    }
}
