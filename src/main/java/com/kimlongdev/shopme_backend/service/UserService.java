package com.kimlongdev.shopme_backend.service;

import com.kimlongdev.shopme_backend.dto.request.RegisterRequest;
import com.kimlongdev.shopme_backend.entity.user.User;
import com.kimlongdev.shopme_backend.exception.BusinessException;

public interface UserService {

    User createUser(RegisterRequest guest) throws BusinessException;
    User findUserByEmail(String email) throws Exception;
    Boolean existsUserByEmail(String email);
}
