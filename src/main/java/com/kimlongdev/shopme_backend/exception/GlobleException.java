package com.kimlongdev.shopme_backend.exception;

import com.kimlongdev.shopme_backend.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobleException {

    @ExceptionHandler(value = {NoResourceFoundException.class})
    public ResponseEntity<ApiResponse<Object>> handleNotFoundException(Exception ex) {
        ApiResponse<Object> apiResponse = new ApiResponse<>();
        apiResponse.setStatus(HttpStatus.NOT_FOUND.value());
        apiResponse.setErrorCode(ex.getMessage());
        apiResponse.setMessage("404 Not Found. URL may not exist...");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }
}
