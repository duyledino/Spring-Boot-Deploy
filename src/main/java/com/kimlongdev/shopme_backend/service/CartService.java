package com.kimlongdev.shopme_backend.service;

import com.kimlongdev.shopme_backend.entity.user.User;

public interface CartService {
    void createNewCart(User user);
}
