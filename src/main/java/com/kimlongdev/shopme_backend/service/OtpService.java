package com.kimlongdev.shopme_backend.service;

public interface OtpService {
    void generateAndSendOtp(String email);
    boolean validateOtp(String email, String otp);
}
