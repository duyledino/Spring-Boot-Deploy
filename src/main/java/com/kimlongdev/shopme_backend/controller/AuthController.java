package com.kimlongdev.shopme_backend.controller;

import com.kimlongdev.shopme_backend.dto.request.*;
import com.kimlongdev.shopme_backend.dto.response.ApiResponse;
import com.kimlongdev.shopme_backend.dto.response.LoginResponse;
import com.kimlongdev.shopme_backend.exception.BusinessException;
import com.kimlongdev.shopme_backend.service.AuthService;
import com.kimlongdev.shopme_backend.service.OtpService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final OtpService otpService;
    private final AuthService authService;

    @PostMapping("/register-otp")
    public ResponseEntity<ApiResponse<?>> registerOtp(
            @Valid @RequestBody OtpRequest req) {
        otpService.sendRegistrationOtp(req.getEmail());
        return ResponseEntity.ok(
                ApiResponse.success(null, "OTP đã được gửi đến email của bạn")
        );
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response
    ) throws BusinessException {

        LoginResponse result = authService.register(request, response);
        return ResponseEntity.ok(ApiResponse.success(result, "Đăng ký thành công"));
    }

    @PostMapping("/login-otp")
    public ResponseEntity<ApiResponse<?>> loginOtp(
            @Valid @RequestBody LoginOtpRequest req) {
        otpService.sendLoginOtp(req.getEmail(), req.getPassword());
        return ResponseEntity.ok(
                ApiResponse.success(null, "OTP đã được gửi đến email của bạn")
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) throws Exception {

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
        otpService.sendPasswordResetOtp(req.getEmail());
        return ResponseEntity.ok(
                ApiResponse.success(null, "OTP đã được gửi đến email của bạn")
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<?>> resetPassword(@RequestBody @Valid LoginRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Đặt lại mật khẩu thành công")
        );
    }

    @PostMapping("/login/social/google")
    public ResponseEntity<ApiResponse<LoginResponse>> loginGoogle(
            @Valid @RequestBody LoginWithGoogleRequest request,
            HttpServletResponse res
    ) throws Exception {
        LoginResponse response = authService.loginWithGoogle(request, res);
        return ResponseEntity.ok(ApiResponse.success(response, "Đăng nhập Google thành công"));
    }

    @PostMapping("/login/social/facebook")
    public ResponseEntity<ApiResponse<LoginResponse>> loginFacebook(
            @Valid @RequestBody LoginWithFaceBookRequest request,
            HttpServletResponse response
    ) throws Exception {
        LoginResponse result = authService.loginWithFacebook(request, response);
        return ResponseEntity.ok(ApiResponse.success(result, "Đăng nhập Facebook thành công"));
    }
}
