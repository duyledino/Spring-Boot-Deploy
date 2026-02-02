package com.kimlongdev.shopme_backend.service.impl;

import com.kimlongdev.shopme_backend.entity.user.User;
import com.kimlongdev.shopme_backend.entity.user.UserStat;
import com.kimlongdev.shopme_backend.repository.UserStatRepository;
import com.kimlongdev.shopme_backend.service.UserStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserStatServiceImpl implements UserStatService {

    private final UserStatRepository userStatRepository;

    @Override
    public void createUserStat(User user) {
        UserStat stats = UserStat.builder()
                .user(user)
                .totalOrders(0)
                .returnedOrdersCount(0)
                .reputationScore(new BigDecimal("100.00"))
                .isRestricted(false)
                .build();
        userStatRepository.save(stats);
    }
}
