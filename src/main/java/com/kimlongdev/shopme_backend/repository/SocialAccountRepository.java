package com.kimlongdev.shopme_backend.repository;

import com.kimlongdev.shopme_backend.entity.user.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
    // Tìm kiếm xem cặp Provider + ID này đã tồn tại chưa
    Optional<SocialAccount> findByProviderAndProviderId(String provider, String providerId);
}