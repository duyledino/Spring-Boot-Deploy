package com.kimlongdev.shopme_backend.service;

public interface OtpService {
    void generateAndSendOtp(String email);
    boolean validateOtp(String email, String otp);

    // Methods với validation logic
    void sendRegistrationOtp(String email);
    void sendLoginOtp(String email, String password);
    void sendPasswordResetOtp(String email);
}
