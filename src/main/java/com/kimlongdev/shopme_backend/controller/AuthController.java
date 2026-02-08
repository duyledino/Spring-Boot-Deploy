package com.kimlongdev.shopme_backend.controller;

import com.kimlongdev.shopme_backend.dto.request.*;
import com.kimlongdev.shopme_backend.dto.response.ApiResponse;
import com.kimlongdev.shopme_backend.dto.response.LoginResponse;
import com.kimlongdev.shopme_backend.exception.BusinessException;
import com.kimlongdev.shopme_backend.service.AuthService;
import com.kimlongdev.shopme_backend.service.OtpService;
import com.kimlongdev.shopme_backend.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final OtpService otpService;
    private final AuthService authService;

    @PostMapping("/register-otp")
    public ResponseEntity<ApiResponse<?>> registerOtp(
            @Valid @RequestBody OtpRequest req) {
        if(userService.existsUserByEmail(req.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "EMAIL_ALREADY_IN_USE", "Email đã được sử dụng"));
        } else {
            otpService.generateAndSendOtp(req.getEmail());

            return ResponseEntity.ok(
                    ApiResponse.success(null, "OTP đã được gửi đến email của bạn")
            );
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) throws BusinessException {

        boolean checkOTP = otpService.validateOtp(request.getEmail(), request.getOtp());

        if (!checkOTP) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "INVALID_OTP", "OTP không hợp lệ hoặc đã hết hạn"));
        }
        LoginResponse result = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(result, "Đăng ký thành công"));
    }

    @PostMapping("/login-otp")
    public ResponseEntity<ApiResponse<?>> loginOtp(
            @Valid @RequestBody OtpRequest req) {

        if(!userService.existsUserByEmail(req.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "EMAIL_IS_NOT_EXIST", "Email không tồn tại"));
        } else {

            boolean isActive = userService.isActive(req.getEmail());

            if (!isActive) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, "USER_BANNED", "Tài khoản của bạn đã bị khóa"));
            }

            otpService.generateAndSendOtp(req.getEmail());

            return ResponseEntity.ok(
                    ApiResponse.success(null, "OTP đã được gửi đến email của bạn")
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) throws Exception {

        boolean checkOTP = otpService.validateOtp(request.getEmail(), request.getOtp());

        if (!checkOTP) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "INVALID_OTP", "OTP không hợp lệ hoặc đã hết hạn"));
        }
        LoginResponse result = authService.login(request, response);
        return ResponseEntity.ok(ApiResponse.success(result, "Đăng nhập thành công"));
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "Missing Token") String refreshToken,
            HttpServletResponse response
    ) throws BusinessException {

        LoginResponse result = authService.refreshToken(refreshToken, response);
        return ResponseEntity.ok(ApiResponse.success(result, "Lấy token mới thành công"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = "refresh_token", defaultValue = "") String refreshToken,
            HttpServletResponse response
    ) {
        authService.logout(refreshToken, response);
        return ResponseEntity.ok(ApiResponse.success(null, "Đăng xuất thành công"));
    }

    @GetMapping("/account")
    public ResponseEntity<ApiResponse<LoginResponse.UserGetAccount>> getAccount() throws Exception {
        return ResponseEntity.ok(ApiResponse.success(authService.getMyAccount()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<?>> forgotPassword(
            @Valid @RequestBody OtpRequest req) {
        if (!userService.existsUserByEmail(req.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "EMAIL_IS_NOT_EXIST", "Email không tồn tại"));
        } else {

            boolean isActive = userService.isActive(req.getEmail());

            if (!isActive) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(
                                400,
                                "USER_BANNED",
                                "Tài khoản của bạn đã bị khóa"
                        ));
            }

            otpService.generateAndSendOtp(req.getEmail());

            return ResponseEntity.ok(
                    ApiResponse.success(null, "OTP đã được gửi đến email của bạn")
            );
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<?>> resetPassword(@RequestBody @Valid LoginRequest request) {

        boolean checkOTP = otpService.validateOtp(request.getEmail(), request.getOtp());

        if (!checkOTP) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "INVALID_OTP", "OTP không hợp lệ hoặc đã hết hạn"));
        }

        boolean success = authService.resetPassword(request.getEmail(), request.getPassword());
        if (!success) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "RESET_PASSWORD_FAILED", "Đặt lại mật khẩu thất bại"));
        }
        return ResponseEntity.ok(
                ApiResponse.success(null, "Đặt lại mật khẩu thành công")
        );
    }

    @PostMapping("/login/social/google")
    public ResponseEntity<ApiResponse<LoginResponse>> loginGoogle(@Valid @RequestBody SocialLoginRequest request) {
        LoginResponse response = authService.loginWithGoogle(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Đăng nhập Google thành công"));
    }
}
