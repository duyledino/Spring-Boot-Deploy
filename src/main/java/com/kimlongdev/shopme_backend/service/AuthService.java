package com.kimlongdev.shopme_backend.service;

import com.kimlongdev.shopme_backend.dto.request.LoginRequest;
import com.kimlongdev.shopme_backend.dto.request.RegisterRequest;
import com.kimlongdev.shopme_backend.dto.request.SocialLoginRequest;
import com.kimlongdev.shopme_backend.dto.response.LoginResponse;
import com.kimlongdev.shopme_backend.exception.BusinessException;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request, HttpServletResponse response) throws Exception;
    LoginResponse register(RegisterRequest request) throws BusinessException;
    void logout(String refreshToken, HttpServletResponse response);
    boolean resetPassword(String email, String newPassword);
    LoginResponse refreshToken(String refreshToken, HttpServletResponse response) throws BusinessException;
    LoginResponse.UserGetAccount getMyAccount() throws Exception;
    LoginResponse loginWithGoogle(SocialLoginRequest request, HttpServletResponse response) throws Exception;
    LoginResponse loginWithFacebook(SocialLoginRequest request, HttpServletResponse response) throws Exception;
}
