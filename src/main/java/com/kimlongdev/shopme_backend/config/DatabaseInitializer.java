package com.kimlongdev.shopme_backend.config;

import com.kimlongdev.shopme_backend.entity.user.User;
import com.kimlongdev.shopme_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

    }

//    private void createAdminIfNotExists() {
//        String adminEmail = "admin@shopme.com";
//
//        if (userRepository.findByEmail(adminEmail).isEmpty()) {
//            User admin = new User();
//            admin.setEmail(adminEmail);
//            admin.setPassword(passwordEncoder.encode("Admin@123"));
//            admin.setFullName("System Administrator");
//            admin.setPhone("0123456789");
//            admin.setRole(User.RoleEnum.ADMIN);
//            admin.setEnabled(true);
//            admin.setCreatedAt(LocalDateTime.now());
//            admin.setUpdatedAt(LocalDateTime.now());
//
//            userRepository.save(admin);
//
//            log.info("✅ Admin account created successfully");
//            log.info("📧 Email: {}", adminEmail);
//            log.info("🔑 Password: Admin@123");
//        } else {
//            log.info("ℹ️ Admin account already exists");
//        }
//    }
}
