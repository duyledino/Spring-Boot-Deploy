package com.kimlongdev.shopme_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SocialLoginRequest {
    @NotBlank(message = "Token không được để trống")
    private String token;
}
