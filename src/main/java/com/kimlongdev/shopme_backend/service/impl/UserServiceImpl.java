package com.kimlongdev.shopme_backend.service.impl;

import com.kimlongdev.shopme_backend.dto.request.RegisterRequest;
import com.kimlongdev.shopme_backend.entity.user.User;
import com.kimlongdev.shopme_backend.exception.BusinessException;
import com.kimlongdev.shopme_backend.repository.UserRepository;
import com.kimlongdev.shopme_backend.service.CartService;
import com.kimlongdev.shopme_backend.service.UserService;
import com.kimlongdev.shopme_backend.service.UserStatService;
import com.kimlongdev.shopme_backend.util.Enum.USER_ROLE;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CartService cartService;
    private final UserStatService userStatService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public User findUserByEmail(String email) throws Exception {
        User user=userRepository.findByEmail(email);

        if(user==null) {
            throw new Exception("user not exist with email " + email);
        }
        return user;
    }

    @Override
    public Boolean existsUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User createUser(RegisterRequest guest) throws BusinessException {
        if (userRepository.existsByEmail(guest.getEmail())) {
            throw new BusinessException(
                    "EMAIL_EXISTS",
                    "Email đã được sử dụng",
                    400
            );
        }

        User newUser = User.builder()
                .fullName(guest.getFullName())
                .email(guest.getEmail())
                .password(passwordEncoder.encode(guest.getPassword()))
                .role(String.valueOf(USER_ROLE.ROLE_CUSTOMER))
                .isActive(true)
                .build();

        User savedUser = userRepository.save(newUser);

        // 4. Tạo Cart rỗng đi kèm (Bắt buộc)
        cartService.createNewCart(savedUser);

        // 5. Tạo UserStats đi kèm (Bắt buộc)
        userStatService.createUserStat(savedUser);

        return savedUser;
    }

}
