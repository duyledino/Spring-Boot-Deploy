package com.kimlongdev.shopme_backend.service.impl;

import com.kimlongdev.shopme_backend.entity.user.User;
import com.kimlongdev.shopme_backend.exception.BusinessException;
import com.kimlongdev.shopme_backend.service.OtpService;
import com.kimlongdev.shopme_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final StringRedisTemplate redisTemplate;
    private final JavaMailSender mailSender;
    private final UserService userService;

    // Cấu hình: OTP hết hạn sau 5 phút
    private static final long OTP_TTL_MINUTES = 5;
    private static final String OTP_PREFIX = "OTP:";
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public void generateAndSendOtp(String email) {
        String otp = generateRandomOtp();

        String redisKey = OTP_PREFIX + email;
        redisTemplate.opsForValue().set(redisKey, otp, Duration.ofMinutes(OTP_TTL_MINUTES));

        sendEmail(email, otp);
    }

    public boolean validateOtp(String email, String otpInput) {
        String redisKey = OTP_PREFIX + email;
        String storedOtp = redisTemplate.opsForValue().get(redisKey);

        // Case 1: OTP hết hạn hoặc không tồn tại
        if (storedOtp == null) {
            return false;
        }

        // Case 2: OTP sai
        if (!storedOtp.equals(otpInput)) {
            return false;
        }

        // Case 3: OTP đúng -> Xóa ngay lập tức để tránh dùng lại (Replay Attack)
        redisTemplate.delete(redisKey);
        return true;
    }

    @Override
    public void sendRegistrationOtp(String email) {
        // Validate: Email đã tồn tại
        if (userService.existsUserByEmail(email)) {
            throw new BusinessException("EMAIL_ALREADY_IN_USE", "Email đã được sử dụng", 400, null);
        }

        generateAndSendOtp(email);
    }

    @Override
    public void sendLoginOtp(String email, String password) {
        // Validate: Email không tồn tại
        if (!userService.existsUserByEmail(email)) {
            throw new BusinessException("EMAIL_IS_NOT_EXIST", "Email không tồn tại", 400, null);
        }

        User user = userService.findUserByEmail(email);

        // Validate: Password (dùng matches, KHÔNG dùng encode + equals)
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("INVALID_CREDENTIALS", "Email hoặc mật khẩu không đúng", 401, null);
        }

        // Validate: Tài khoản bị khóa
        if (!user.getIsActive()) {
            throw new BusinessException("USER_BANNED", "Tài khoản của bạn đã bị khóa", 403, null);
        }

        generateAndSendOtp(email);
    }

    @Override
    public void sendPasswordResetOtp(String email) {
        // Validate: Email không tồn tại
        if (!userService.existsUserByEmail(email)) {
            throw new BusinessException("EMAIL_IS_NOT_EXIST", "Email không tồn tại", 400, null);
        }

        // Validate: Tài khoản bị khóa
        if (!userService.isActive(email)) {
            throw new BusinessException("USER_BANNED", "Tài khoản của bạn đã bị khóa", 403, null);
        }

        generateAndSendOtp(email);
    }

    // --- Private Helpers ---

    private String generateRandomOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    private void sendEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[ShopMe] Mã xác thực OTP của bạn");
        message.setText("Mã OTP của bạn là: " + otp + "\n\nMã này có hiệu lực trong 5 phút. Vui lòng không chia sẻ cho ai khác.");
        mailSender.send(message);
    }
}
