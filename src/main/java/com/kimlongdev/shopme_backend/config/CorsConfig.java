package com.kimlongdev.shopme_backend.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(Arrays.asList(
                    "http://localhost:8080",
                    "http://localhost:3000",
                    "http://localhost:5173"));
            //config.setAllowedOrigins(Collections.singletonList("*")); // Allow all domains
            config.setAllowedMethods(Collections.singletonList("*")); // Allow all HTTP methods
            config.setAllowedHeaders(Collections.singletonList("*")); // Allow all headers
            config.setAllowCredentials(true ); // Allow sending cookies
            config.setExposedHeaders(Collections.singletonList("Authorization")); // Frontend can read the Authorization header
            config.setMaxAge(3600L); // Cache preflight response for 1 hour

            return config;
        };
    }
}
