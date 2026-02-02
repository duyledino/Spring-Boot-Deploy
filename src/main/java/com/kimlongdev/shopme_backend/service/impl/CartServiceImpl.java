package com.kimlongdev.shopme_backend.service.impl;

import com.kimlongdev.shopme_backend.entity.cart.Cart;
import com.kimlongdev.shopme_backend.entity.user.User;
import com.kimlongdev.shopme_backend.repository.CartRepository;
import com.kimlongdev.shopme_backend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private  final CartRepository cartRepository;

    @Override
    public void createNewCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);
    }
}
