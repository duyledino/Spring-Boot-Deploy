package com.kimlongdev.shopme_backend.service.impl;

import com.kimlongdev.shopme_backend.entity.user.Token;
import com.kimlongdev.shopme_backend.entity.user.User;
import com.kimlongdev.shopme_backend.repository.TokenRepository;
import com.kimlongdev.shopme_backend.service.TokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;

    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public void saveRefreshToken(User user, String refreshToken) {
        // Revoke tất cả token cũ của user
        revokeAllUserTokens(user);

        // Lưu refreshToken mới
        Token token = Token.builder()
                .user(user)
                .refreshToken(refreshToken)
                .expired(false)
                .revoked(false)
                .build();

        tokenRepository.save(token);
    }

    public Optional<Token> findByRefreshToken(String refreshToken) {
        return tokenRepository.findByRefreshToken(refreshToken);
    }

    public void revokeAllUserTokens(User user) {
        List<Token> validTokens = tokenRepository.findAllValidTokenByUser(user.getUserId());
        if (validTokens.isEmpty()) return;

        validTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validTokens);
    }

    public void rotateRefreshToken(Token storedToken) {
        storedToken.setRevoked(true);
        storedToken.setExpired(true);
        tokenRepository.save(storedToken);
    }

    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void revokeToken(String token) {
        if (token != null && !token.isEmpty()) {
            tokenRepository.findByRefreshToken(token).ifPresent(storedToken -> {
                storedToken.setRevoked(true);
                storedToken.setExpired(true);
                tokenRepository.save(storedToken);
            });
        }
    }
}
