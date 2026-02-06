package com.kimlongdev.shopme_backend.service;

import com.kimlongdev.shopme_backend.entity.user.SocialAccount;
import com.kimlongdev.shopme_backend.entity.user.User;

import java.util.Optional;

public interface SocialAccountService {
    void createSocialAccount(User user, String provider, String providerId);
    Optional<SocialAccount> findByProviderAndProviderId(String providerId, String providerProviderId);
}
