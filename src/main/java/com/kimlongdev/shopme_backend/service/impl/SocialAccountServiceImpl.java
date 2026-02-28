package com.kimlongdev.shopme_backend.service.impl;

import com.kimlongdev.shopme_backend.entity.user.SocialAccount;
import com.kimlongdev.shopme_backend.entity.user.User;
import com.kimlongdev.shopme_backend.repository.SocialAccountRepository;
import com.kimlongdev.shopme_backend.service.SocialAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SocialAccountServiceImpl implements SocialAccountService {
    private final SocialAccountRepository socialAccountRepository;

    @Override
    public Optional<SocialAccount> findByProviderAndProviderId(String provider, String providerId) {
        return socialAccountRepository.findByProviderAndProviderId(provider, providerId);
    }

    @Override
    public void createSocialAccount(User user, String provider, String providerId) {
        SocialAccount newSocialAccount = SocialAccount.builder()
                .user(user)
                .provider(provider)
                .providerId(providerId)
                .email(user.getEmail())
                .name(user.getFullName())
                .build();

        socialAccountRepository.save(newSocialAccount);
    }
}
