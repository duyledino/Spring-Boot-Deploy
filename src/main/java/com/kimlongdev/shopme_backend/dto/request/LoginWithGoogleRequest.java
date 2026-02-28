package com.kimlongdev.shopme_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginWithGoogleRequest {
    @NotBlank(message = "Token không được để trống")
    private String token;
}
