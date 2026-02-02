package com.kimlongdev.shopme_backend.exception;

import com.kimlongdev.shopme_backend.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalException {

    // Handle validation errors (@Valid trên request body)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationErrors() {
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode("VALIDATION_ERROR")
                .message("Dữ liệu không hợp lệ")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    // Handle constraint violations (@Valid trên method parameters)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolation() {

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode("VALIDATION_ERROR")
                .message("Dữ liệu không hợp lệ")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    // Handle 404 Not Found
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFoundException(
            NoResourceFoundException ex
    ) {
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .errorCode("NOT_FOUND")
                .message("URL không tồn tại: " + ex.getResourcePath())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    // Handle tất cả exceptions khác
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(
            Exception ex
    ) {
        // Log để debug (không expose ra client)
        ex.printStackTrace();

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorCode("INTERNAL_SERVER_ERROR")
                .message("Đã xảy ra lỗi hệ thống")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }
}
