package com.kimlongdev.shopme_backend.service;

import com.kimlongdev.shopme_backend.entity.user.Token;
import com.kimlongdev.shopme_backend.entity.user.User;

import java.util.Optional;

public interface TokenService {
    void saveRefreshToken(User user, String refreshToken);
    void revokeAllUserTokens(User user);
    Optional<Token> findByRefreshToken(String refreshToken);
}
