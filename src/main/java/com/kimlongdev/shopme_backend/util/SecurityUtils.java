package com.kimlongdev.shopme_backend.util;

import com.kimlongdev.shopme_backend.entity.user.User;
import com.kimlongdev.shopme_backend.dto.response.LoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;

@Service
public class SecurityUtils {

    // Thuật toán mã hóa mạnh HS512 (yêu cầu key > 64 bytes)
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    @Value("${app.jwt.secret-key}")
    private String jwtKey;

    @Value("${app.jwt.expiration}")
    private long accessTokenExpiration;

    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private final JwtEncoder jwtEncoder;

    public SecurityUtils(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    /**
     * Tạo Access Token (Ngắn hạn, chứa nhiều thông tin)
     */
    public String createAccessToken(User user) {
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();

        Instant now = Instant.now();
        Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.MILLIS);

        LoginResponse.UserInsideToken userTokenData = LoginResponse.UserInsideToken.builder()
                .id(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(user.getEmail())
                .claim("user", userTokenData)
                .claim("scope", Collections.singletonList(user.getRole()))
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public String createRefreshToken(User user) {
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();

        Instant now = Instant.now();
        Instant validity = now.plus(this.refreshTokenExpiration, ChronoUnit.MILLIS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(user.getEmail())
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    /**
     * Validate Refresh Token thủ công (dành cho API /refresh-token)
     */
    public Jwt checkValidRefreshToken(String token) {
        // Tạo decoder cục bộ với Secret Key
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                getSecretKey()).macAlgorithm(SecurityUtils.JWT_ALGORITHM).build();
        try {
            return jwtDecoder.decode(token);
        } catch (JwtException ex) {
            System.out.println(">>> Refresh Token invalid: " + ex.getMessage());
            throw ex;
        }
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtKey);
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
    }

    // ========================================================================
    // UTILS ĐỂ LẤY USER ĐANG LOGIN (Dùng trong Service/Controller)
    // ========================================================================

    /**
     * Lấy Email người dùng hiện tại từ SecurityContext
     */
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        // 1. Trường hợp đăng nhập cơ bản (UserDetails)
        if (principal instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        }

        // 2. Trường hợp dùng JWT (Oauth2 Resource Server)
        else if (principal instanceof Jwt jwt) {
            return jwt.getSubject(); // Lấy từ claim "sub" (email)
        }

        // 3. Trường hợp String (Cần lọc bỏ anonymousUser)
        else if (principal instanceof String s) {
            if ("anonymousUser".equals(s)) {
                return null;
            }
            return s;
        }

        return null;
    }

    /**
     * Lấy raw JWT token hiện tại
     */
    public static Optional<String> getCurrentUserJWT() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .filter(authentication -> authentication.getCredentials() instanceof Jwt)
                .map(authentication -> ((Jwt) authentication.getCredentials()).getTokenValue());
    }
}