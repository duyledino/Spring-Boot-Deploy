package com.kimlongdev.shopme_backend.service;

import com.kimlongdev.shopme_backend.dto.request.LoginRequest;
import com.kimlongdev.shopme_backend.dto.request.LoginWithFaceBookRequest;
import com.kimlongdev.shopme_backend.dto.request.RegisterRequest;
import com.kimlongdev.shopme_backend.dto.request.LoginWithGoogleRequest;
import com.kimlongdev.shopme_backend.dto.response.LoginResponse;
import com.kimlongdev.shopme_backend.exception.BusinessException;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request, HttpServletResponse response) throws Exception;
    LoginResponse register(RegisterRequest request, HttpServletResponse response) throws BusinessException;
    void logout(String refreshToken, HttpServletResponse response);
    void resetPassword(LoginRequest request) throws BusinessException;
    LoginResponse refreshToken(String refreshToken, HttpServletResponse response) throws BusinessException;
    LoginResponse.UserGetAccount getMyAccount() throws Exception;
    LoginResponse loginWithGoogle(LoginWithGoogleRequest request, HttpServletResponse response) throws Exception;
    LoginResponse loginWithFacebook(LoginWithFaceBookRequest request, HttpServletResponse response) throws Exception;

}
