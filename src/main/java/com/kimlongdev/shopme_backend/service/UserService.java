package com.kimlongdev.shopme_backend.service;

import com.kimlongdev.shopme_backend.dto.request.RegisterRequest;
import com.kimlongdev.shopme_backend.entity.user.User;
import com.kimlongdev.shopme_backend.exception.BusinessException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public interface UserService {

    User createUser(RegisterRequest guest) throws BusinessException;
    User findUserByEmail(String email);
    Boolean existsUserByEmail(String email);
    User createUserFromSocial(String fullName, String email, String avatar);

    boolean isActive(@NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email);
}
