package com.kimlongdev.shopme_backend.service.impl;

import com.kimlongdev.shopme_backend.entity.user.Token;
import com.kimlongdev.shopme_backend.entity.user.User;
import com.kimlongdev.shopme_backend.repository.TokenRepository;
import com.kimlongdev.shopme_backend.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;

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
}
