package com.kimlongdev.shopme_backend.service.impl;

import com.kimlongdev.shopme_backend.dto.request.RegisterRequest;
import com.kimlongdev.shopme_backend.dto.response.ApiResponse;
import com.kimlongdev.shopme_backend.dto.response.LoginResponse;
import com.kimlongdev.shopme_backend.entity.cart.Cart;
import com.kimlongdev.shopme_backend.entity.user.Token;
import com.kimlongdev.shopme_backend.entity.user.User;
import com.kimlongdev.shopme_backend.entity.user.UserStat;
import com.kimlongdev.shopme_backend.exception.BusinessException;
import com.kimlongdev.shopme_backend.repository.CartRepository;
import com.kimlongdev.shopme_backend.repository.TokenRepository;
import com.kimlongdev.shopme_backend.repository.UserRepository;
import com.kimlongdev.shopme_backend.repository.UserStatRepository;
import com.kimlongdev.shopme_backend.service.AuthService;
import com.kimlongdev.shopme_backend.service.TokenService;
import com.kimlongdev.shopme_backend.service.UserService;
import com.kimlongdev.shopme_backend.util.Enum.USER_ROLE;
import com.kimlongdev.shopme_backend.util.SecurityUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final SecurityUtil securityUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    // --- 1. LOGIN ---
//    @Override
//    @Transactional
//    public LoginResponse login(LoginRequest request, HttpServletResponse response) {
//        // A. Spring Security Authenticate
//        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
//                request.getEmail(), request.getPassword());
//        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // B. Lấy User từ DB
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new AppException("USER_NOT_FOUND", "Tài khoản không tồn tại", 404));
//
//        // C. Sinh Token
//        String accessToken = securityUtil.createAccessToken(user);
//        String refreshToken = securityUtil.createRefreshToken(user);
//
//        // D. Lưu Refresh Token vào bảng 'tokens' (Stateful)
//        saveUserToken(user, refreshToken);
//
//        // E. Set Cookie HttpOnly
//        setRefreshTokenCookie(response, refreshToken);
//
//        // F. Trả về Response
//        return LoginResponse.builder()
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .user(LoginResponse.UserInfo.fromEntity(user))
//                .build();
//    }

    // --- 2. REGISTER (Kèm Cart & Stats) ---
    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request) throws BusinessException {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ApiResponse.error(400, "EMAIL_EXISTS", "Email đã được sử dụng"));
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
//
//    // --- 3. REFRESH TOKEN ---
//    @Override
//    public LoginResponse refreshToken(String refreshToken, HttpServletResponse response) {
//        if (refreshToken == null || refreshToken.equals("Missing Token")) {
//            throw new AppException("MISSING_COOKIE", "Không tìm thấy Refresh Token", 400);
//        }
//
//        // A. Check Token trong DB (Quan trọng)
//        Token storedToken = tokenRepository.findByRefreshToken(refreshToken)
//                .orElseThrow(() -> new AppException("INVALID_TOKEN", "Token không tồn tại hoặc đã bị xóa", 401));
//
//        if (storedToken.getExpired() || storedToken.getRevoked()) {
//            throw new AppException("TOKEN_REVOKED", "Phiên đăng nhập đã hết hạn", 401);
//        }
//
//        // B. Verify JWT Signature
//        Jwt decodedToken = securityUtil.checkValidRefreshToken(refreshToken);
//        String email = decodedToken.getSubject();
//
//        // C. Rotate Token (Thu hồi cái cũ, cấp cái mới)
//        storedToken.setRevoked(true);
//        storedToken.setExpired(true);
//        tokenRepository.save(storedToken);
//
//        User user = storedToken.getUser();
//        String newAccessToken = securityUtil.createAccessToken(user);
//        String newRefreshToken = securityUtil.createRefreshToken(user);
//
//        saveUserToken(user, newRefreshToken);
//        setRefreshTokenCookie(response, newRefreshToken);
//
//        return LoginResponse.builder()
//                .accessToken(newAccessToken)
//                .refreshToken(newRefreshToken)
//                .user(LoginResponse.UserInfo.fromEntity(user))
//                .build();
//    }
//
//    // --- 4. LOGOUT ---
//    @Override
//    @Transactional
//    public void logout(String refreshToken, HttpServletResponse response) {
//        // A. Revoke token trong DB
//        if (refreshToken != null && !refreshToken.isEmpty()) {
//            tokenRepository.findByRefreshToken(refreshToken).ifPresent(token -> {
//                token.setRevoked(true);
//                token.setExpired(true);
//                tokenRepository.save(token);
//            });
//        }
//
//        // B. Xóa Cookie
//        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
//                .httpOnly(true)
//                .secure(true)
//                .path("/")
//                .maxAge(0)
//                .build();
//        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
//    }
//
//    // --- 5. GET ACCOUNT ---
//    @Override
//    public LoginResponse.UserGetAccount getMyAccount() {
//        String email = SecurityUtil.getCurrentUserLogin()
//                .orElseThrow(() -> new AppException("UNAUTHORIZED", "Chưa đăng nhập", 401));
//
//        User user = userRepository.findByEmail(email).orElseThrow();
//        return new LoginResponse.UserGetAccount(LoginResponse.UserInfo.fromEntity(user));
//    }
//
//    // --- Helpers ---
//    private void saveUserToken(User user, String refreshToken) {
//        Token token = Token.builder()
//                .user(user)
//                .refreshToken(refreshToken)
//                .expired(false)
//                .revoked(false)
//                .build();
//        tokenRepository.save(token);
//    }
//
//    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
//        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
//                .httpOnly(true)
//                .secure(true)
//                .path("/")
//                .maxAge(refreshTokenExpiration) // Dùng giây (lưu ý config của bạn)
//                .build();
//        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
//    }
}