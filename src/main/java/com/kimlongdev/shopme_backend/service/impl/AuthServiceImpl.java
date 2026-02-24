package com.kimlongdev.shopme_backend.service.impl;

import com.kimlongdev.shopme_backend.dto.request.LoginRequest;
import com.kimlongdev.shopme_backend.dto.request.LoginWithFaceBookRequest;
import com.kimlongdev.shopme_backend.dto.request.RegisterRequest;
import com.kimlongdev.shopme_backend.dto.request.LoginWithGoogleRequest;
import com.kimlongdev.shopme_backend.dto.response.LoginResponse;
import com.kimlongdev.shopme_backend.entity.user.SocialAccount;
import com.kimlongdev.shopme_backend.entity.user.Token;
import com.kimlongdev.shopme_backend.entity.user.User;
import com.kimlongdev.shopme_backend.exception.BusinessException;
import com.kimlongdev.shopme_backend.service.*;
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
    private final OtpService otpService;

    private LoginResponse buildLoginResponse(User user, HttpServletResponse response) {

        // Sinh Token
        String accessToken = securityUtils.createAccessToken(user);
        String refreshToken = securityUtils.createRefreshToken(user);

        // Lưu Refresh Token vào bảng 'tokens'
        tokenService.saveRefreshToken(user, refreshToken);
        tokenService.setRefreshTokenCookie(response, refreshToken);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .user(LoginResponse.UserInfo.fromEntity(user))
                .build();
    }


    // --- 1. LOGIN ---
    @Override
    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletResponse response) {

        boolean checkOTP = otpService.validateOtp(request.getEmail(), request.getOtp());

        if (!checkOTP) {
            throw new BusinessException("INVALID_OTP", "Mã OTP không hợp lệ", 400, null);
        }
        // Spring Security Authenticate
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userService.findUserByEmail(request.getEmail());

        return buildLoginResponse(user, response);
    }

    // --- 2. REGISTER (Kèm Cart & Stats) ---
    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request, HttpServletResponse response) throws BusinessException {
        boolean checkOTP = otpService.validateOtp(request.getEmail(), request.getOtp());

        if (!checkOTP) {
            throw new BusinessException("INVALID_OTP", "Mã OTP không hợp lệ", 400, null);
        }

        if (userService.existsUserByEmail(request.getEmail())) {
            throw new BusinessException("EMAIL_EXISTS", "Email đã được sử dụng", 400, null);
        }
        User user = userService.createUser(request);

        return buildLoginResponse(user, response);
    }

    // --- REFRESH TOKEN ---
    @Override
    @Transactional
    public LoginResponse refreshToken(String refreshToken, HttpServletResponse response) throws BusinessException {
        if (refreshToken == null || refreshToken.equals("Missing Token")) {
            throw new BusinessException(
                    "TOKEN_MISSING",
                    "Token không được để trống",
                    401,
                    null
            );
        }

        // Layer 1: Validate JWT signature & expiration (không cần DB)
        try {
            securityUtils.checkValidRefreshToken(refreshToken);
        } catch (Exception e) {
            throw new BusinessException(
                    "INVALID_TOKEN",
                    "Token không hợp lệ hoặc đã hết hạn",
                    401,
                    null
            );
        }

        // Layer 2: Check Token trong DB (đảm bảo chưa bị revoke)
        Token storedToken = tokenService.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BusinessException(
                        "TOKEN_NOT_FOUND",
                        "Token không tồn tại",
                        401,
                        null
                ));


        if (storedToken.getExpired() || storedToken.getRevoked()) {
            throw new BusinessException(
                    "TOKEN_REVOKED",
                    "Phiên đăng nhập đã hết hạn",
                    401,
                    null
            );
        }

        // Rotate Token (Thu hồi cái cũ, cấp cái mới)
        tokenService.rotateRefreshToken(storedToken);

        User user = storedToken.getUser();

        return buildLoginResponse(user, response);
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
                        401,
                        null
                ));

        User user = userService.findUserByEmail(email);
        return new LoginResponse.UserGetAccount(LoginResponse.UserInfo.fromEntity(user));
    }

    @Override
    @Transactional
    public LoginResponse loginWithGoogle(LoginWithGoogleRequest request, HttpServletResponse response) {
        try {
            GoogleUtils.GoogleUserInfo userInfo = googleUtils.getUserInfoFromAccessToken(request.getToken());

            if (userInfo == null || userInfo.getEmail() == null) {
                throw new BusinessException("INVALID_GOOGLE_TOKEN", "Token Google không hợp lệ", 400, null);
            }

            String email = userInfo.getEmail();
            String name = userInfo.getName();
            String picture = userInfo.getPicture();
            String providerId = userInfo.getId();

            // Business Logic
            User user = resolveUser(email, providerId, name, picture, "GOOGLE");

            boolean isActive = userService.isActive(user.getEmail());
            if (!isActive) {
                throw new BusinessException("USER_BANNED", "Tài khoản của bạn đã bị khóa", 400, null);
            }

            return buildLoginResponse(user, response);

        } catch (Exception e) {
            throw new BusinessException("GOOGLE_LOGIN_FAILED", "Đăng nhập Google thất bại: " + e.getMessage(), 500, null);
        }
    }

    @Override
    @Transactional
    public LoginResponse loginWithFacebook(
            LoginWithFaceBookRequest request,
            HttpServletResponse response
    ) {
        try {
            // Validate request
            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                throw new BusinessException(
                        "FACEBOOK_EMAIL_REQUIRED",
                        "Email là bắt buộc",
                        400,
                        null
                );
            }

            if (request.getProviderId() == null || request.getProviderId().isEmpty()) {
                throw new BusinessException(
                        "FACEBOOK_PROVIDER_ID_REQUIRED",
                        "Provider ID là bắt buộc",
                        400,
                        null
                );
            }

            String email = request.getEmail();
            String providerId = request.getProviderId();
            String name = request.getName();
            String picture = request.getAvatarUrl();
            String provider = "FACEBOOK";

            // Xử lý user
            User user = resolveUser(email, providerId, name, picture, provider);

            // Check active
            if (!user.getIsActive()) {
                throw new BusinessException("USER_BANNED", "Tài khoản của bạn đã bị khóa", 403, null);
            }

            return buildLoginResponse(user, response);

        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            throw new BusinessException("FACEBOOK_LOGIN_FAILED", "Đăng nhập Facebook thất bại: " + e.getMessage(), 500, null);
        }
    }

    private User resolveUser(String email, String providerId, String name, String avatar, String providerName) {

        // Tìm trong bảng SocialAccount theo Provider Name dynamic
        Optional<SocialAccount> socialAccountOpt =
                socialAccountService.findByProviderAndProviderId(providerName, providerId);

        if (socialAccountOpt.isPresent()) {
            return socialAccountOpt.get().getUser();
        }

        // Nếu không tìm thấy, kiểm tra User theo email
        User userByEmail = userService.findUserByEmail(email);
        User user;

        if (userByEmail != null) {
            user = userByEmail;
        } else {
            user = userService.createUserFromSocial(name, email, avatar);
        }

        // Tạo liên kết SocialAccount với Provider Name dynamic
        socialAccountService.createSocialAccount(user, providerName, providerId);

        return user;
    }

    @Override
    @Transactional
    public void resetPassword(LoginRequest request) throws BusinessException {
        // Validate OTP
        boolean checkOTP = otpService.validateOtp(request.getEmail(), request.getOtp());
        if (!checkOTP) {
            throw new BusinessException("INVALID_OTP", "Mã OTP không hợp lệ", 400, null);
        }

        // Validate user exists
        User user = userService.findUserByEmail(request.getEmail());
        if (user == null) {
            throw new BusinessException("USER_NOT_FOUND", "Người dùng không tồn tại", 404, null);
        }

        // Update password
        boolean success = userService.updateUserPassword(user, request.getPassword());
        if (!success) {
            throw new BusinessException("RESET_PASSWORD_FAILED", "Đặt lại mật khẩu thất bại", 500, null);
        }
    }
}