package com.kimlongdev.shopme_backend.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Component
public class GoogleUtils {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    private final RestTemplate restTemplate = new RestTemplate();

    public GoogleIdToken.Payload verifyToken(String idTokenString) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            return idToken.getPayload();
        }
        return null;
    }

    public GoogleUserInfo getUserInfoFromAccessToken(String accessToken) {
        String url = "https://www.googleapis.com/oauth2/v2/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<GoogleUserInfo> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    GoogleUserInfo.class
            );

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get user info from Google: " + e.getMessage());
        }
    }

    @Data
    public static class GoogleUserInfo {
        private String id;          // providerId
        private String email;
        private String name;
        private String picture;
        private Boolean verifiedEmail;
    }
}
