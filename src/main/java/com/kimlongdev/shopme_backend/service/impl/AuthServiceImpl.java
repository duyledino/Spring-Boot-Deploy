package com.kimlongdev.shopme_backend.service.impl;

import com.kimlongdev.shopme_backend.dto.request.LoginRequest;
import com.kimlongdev.shopme_backend.dto.request.RegisterRequest;
import com.kimlongdev.shopme_backend.dto.response.LoginResponse;
import com.kimlongdev.shopme_backend.entity.user.Token;
import com.kimlongdev.shopme_backend.entity.user.User;
import com.kimlongdev.shopme_backend.exception.BusinessException;
import com.kimlongdev.shopme_backend.service.AuthService;
import com.kimlongdev.shopme_backend.service.TokenService;
import com.kimlongdev.shopme_backend.service.UserService;
import com.kimlongdev.shopme_backend.util.SecurityUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;
    private final TokenService tokenService;
    private final SecurityUtil securityUtil;

    // --- 1. LOGIN ---
    @Override
    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletResponse response) throws Exception {
        // Spring Security Authenticate
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userService.findUserByEmail(request.getEmail());

        // Sinh Token
        String accessToken = securityUtil.createAccessToken(user);
        String refreshToken = securityUtil.createRefreshToken(user);

        // Lưu Refresh Token vào bảng 'tokens'
        tokenService.saveRefreshToken(user, refreshToken);

        // E. Set Cookie HttpOnly
        tokenService.setRefreshTokenCookie(response, refreshToken);

        // F. Trả về Response
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(LoginResponse.UserInfo.fromEntity(user))
                .build();
    }

    // --- 2. REGISTER (Kèm Cart & Stats) ---
    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request) throws BusinessException {
        if (userService.existsUserByEmail(request.getEmail())) {
            throw new BusinessException("EMAIL_EXISTS", "Email đã được sử dụng", 400);
        }
        User user = userService.createUser(request);

        // 2. Tự động sinh Token (Auto Login)
        String accessToken = securityUtil.createAccessToken(user);
        String refreshToken = securityUtil.createRefreshToken(user);

        tokenService.saveRefreshToken(user, refreshToken);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .user(LoginResponse.UserInfo.fromEntity(user))
                .build();
    }

    // --- REFRESH TOKEN ---
    @Override
    @Transactional
    public LoginResponse refreshToken(String refreshToken, HttpServletResponse response) throws BusinessException {
        if (refreshToken == null || refreshToken.equals("Missing Token")) {
            throw new BusinessException(
                    "TOKEN_MISSING",
                    "Token không được để trống",
                    401
            );
        }

        // Check Token trong DB
        Token storedToken = tokenService.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BusinessException(
                        "TOKEN_NOT_FOUND",
                        "Token không tồn tại",
                        401
                ));


        if (storedToken.getExpired() || storedToken.getRevoked()) {
            throw new BusinessException(
                    "TOKEN_REVOKED",
                    "Phiên đăng nhập đã hết hạn",
                    401
            );
        }

        // Rotate Token (Thu hồi cái cũ, cấp cái mới)
        tokenService.rotateRefreshToken(storedToken);

        User user = storedToken.getUser();
        String newAccessToken = securityUtil.createAccessToken(user);
        String newRefreshToken = securityUtil.createRefreshToken(user);

        tokenService.saveRefreshToken(user, newRefreshToken);
        tokenService.setRefreshTokenCookie(response, newRefreshToken);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .user(LoginResponse.UserInfo.fromEntity(user))
                .build();
    }

    // --- LOGOUT ---
    @Override
    @Transactional
    public void logout(String refreshToken, HttpServletResponse response) {
        // Revoke token trong DB
        tokenService.revokeToken(refreshToken);

        // Xóa Cookie
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // --- GET ACCOUNT ---
    @Override
    public LoginResponse.UserGetAccount getMyAccount() throws Exception {
        String email = SecurityUtil.getCurrentUserLogin()
                .filter(username -> !username.equals("anonymousUser"))
                .orElseThrow(() -> new BusinessException(
                        "UNAUTHORIZED",
                        "Vui lòng đăng nhập để tiếp tục",
                        401
                ));

        User user = userService.findUserByEmail(email);
        return new LoginResponse.UserGetAccount(LoginResponse.UserInfo.fromEntity(user));
    }
}