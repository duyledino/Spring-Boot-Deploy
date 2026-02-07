package com.kimlongdev.shopme_backend.service.impl;

import com.kimlongdev.shopme_backend.dto.request.LoginRequest;
import com.kimlongdev.shopme_backend.dto.request.RegisterRequest;
import com.kimlongdev.shopme_backend.dto.request.SocialLoginRequest;
import com.kimlongdev.shopme_backend.dto.response.LoginResponse;
import com.kimlongdev.shopme_backend.entity.user.SocialAccount;
import com.kimlongdev.shopme_backend.entity.user.Token;
import com.kimlongdev.shopme_backend.entity.user.User;
import com.kimlongdev.shopme_backend.exception.BusinessException;
import com.kimlongdev.shopme_backend.service.AuthService;
import com.kimlongdev.shopme_backend.service.SocialAccountService;
import com.kimlongdev.shopme_backend.service.TokenService;
import com.kimlongdev.shopme_backend.service.UserService;
import com.kimlongdev.shopme_backend.util.GoogleUtils;
import com.kimlongdev.shopme_backend.util.SecurityUtils;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;
    private final TokenService tokenService;
    private final SecurityUtils securityUtils;
    private final GoogleUtils googleUtils;
    private final SocialAccountService socialAccountService;

    // --- 1. LOGIN ---
    @Override
    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletResponse response) {
        // Spring Security Authenticate
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userService.findUserByEmail(request.getEmail());

        // Sinh Token
        String accessToken = securityUtils.createAccessToken(user);
        String refreshToken = securityUtils.createRefreshToken(user);

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
        String accessToken = securityUtils.createAccessToken(user);
        String refreshToken = securityUtils.createRefreshToken(user);

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
        String newAccessToken = securityUtils.createAccessToken(user);
        String newRefreshToken = securityUtils.createRefreshToken(user);

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
    public LoginResponse.UserGetAccount getMyAccount() {
        String email = SecurityUtils.getCurrentUserLogin()
                .filter(username -> !username.equals("anonymousUser"))
                .orElseThrow(() -> new BusinessException(
                        "UNAUTHORIZED",
                        "Vui lòng đăng nhập để tiếp tục",
                        401
                ));

        User user = userService.findUserByEmail(email);
        return new LoginResponse.UserGetAccount(LoginResponse.UserInfo.fromEntity(user));
    }

    @Override
    @Transactional
    public LoginResponse loginWithGoogle(SocialLoginRequest request) {
        try {
            GoogleUtils.GoogleUserInfo userInfo = googleUtils.getUserInfoFromAccessToken(request.getToken());

            if (userInfo == null || userInfo.getEmail() == null) {
                throw new BusinessException("INVALID_GOOGLE_TOKEN", "Token Google không hợp lệ", 400);
            }

            String email = userInfo.getEmail();
            String name = userInfo.getName();
            String picture = userInfo.getPicture();
            String providerId = userInfo.getId();

            // Business Logic
            User user = resolveUser(email,providerId, name, picture);

            boolean isActive = userService.isActive(user.getEmail());
            if (!isActive) {
                throw new BusinessException("USER_BANNED", "Tài khoản của bạn đã bị khóa", 400);
            }

            // Generate tokens
            String accessToken = securityUtils.createAccessToken(user);
            String refreshToken = securityUtils.createRefreshToken(user);

            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .user(LoginResponse.UserInfo.fromEntity(user))
                    .build();

        } catch (Exception e) {
            throw new BusinessException("GOOGLE_LOGIN_FAILED", "Đăng nhập Google thất bại: " + e.getMessage(), 500);
        }
    }

//
//    @Transactional
//    public LoginResponse loginWithGoogle(SocialLoginRequest request) {
//        try {
//            // Verify Token
//            var payload = googleUtils.verifyToken(request.getAccess_token());
//            if (payload == null)
//                throw new BusinessException("INVALID_GOOGLE_TOKEN", "Token Google không hợp lệ", 400);
//
//            String providerId = payload.getSubject();
//            String email = payload.getEmail();
//            String name = (String) payload.get("name");
//            String pictureUrl = (String) payload.get("picture");
//
//            // Resolve User (Business Logic)
//            User user = resolveUser(email, providerId, name, pictureUrl);
//
//            boolean isActive = userService.isActive(user.getEmail());
//            if (!isActive) {
//                throw new BusinessException("USER_BANNED", "Tài khoản của bạn đã bị khóa", 400);
//            }
//
//            // Generate Token
//            String accessToken = securityUtils.createAccessToken(user);
//            String refreshToken = securityUtils.createRefreshToken(user);
//
//            tokenService.saveRefreshToken(user, refreshToken);
//
//            return LoginResponse.builder()
//                    .accessToken(accessToken)
//                    .refreshToken(refreshToken)
//                    .user(LoginResponse.UserInfo.fromEntity(user))
//                    .build();
//
//        } catch (Exception e) {
//            throw new BusinessException("GOOGLE_LOGIN_FAILED", "Đăng nhập Google thất bại: " + e.getMessage(), 500);
//        }
//    }

    private User resolveUser(String email, String providerId, String name, String avatar){
        // Đã từng login Google rồi -> Tìm thấy trong bảng SocialAccount
        Optional<SocialAccount> socialAccountOpt =
                socialAccountService.findByProviderAndProviderId("GOOGLE", providerId);

        if (socialAccountOpt.isPresent()) {
            return socialAccountOpt.get().getUser();
        }

        // Nếu chưa có trong bảng Social -> Check tiếp bảng User
        User userByEmail = userService.findUserByEmail(email);
        User user;

        if (userByEmail != null) {
            // Đã có tài khoản User (do đăng kí bằng email hoặc FB) -> Link vào
            user = userByEmail;
        } else {
            // Tạo User mới
            user = userService.createUserFromSocial(name, email, avatar);
        }

        // Sau khi có User (dù mới hay cũ) -> Tạo liên kết SocialAccount
        socialAccountService.createSocialAccount(user, "GOOGLE", providerId);

        return user;
    }
}