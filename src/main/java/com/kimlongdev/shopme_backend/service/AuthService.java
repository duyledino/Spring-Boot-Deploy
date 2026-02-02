package com.kimlongdev.shopme_backend.service;

import com.kimlongdev.shopme_backend.dto.request.LoginRequest;
import com.kimlongdev.shopme_backend.dto.request.RegisterRequest;
import com.kimlongdev.shopme_backend.dto.response.LoginResponse;
import com.kimlongdev.shopme_backend.exception.BusinessException;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request, HttpServletResponse response) throws Exception;
    LoginResponse register(RegisterRequest request) throws BusinessException;
}
