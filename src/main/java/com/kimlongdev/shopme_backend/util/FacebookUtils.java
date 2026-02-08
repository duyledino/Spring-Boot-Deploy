package com.kimlongdev.shopme_backend.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class FacebookUtils {

    @Value("${spring.security.oauth2.client.provider.facebook.user-info-uri}")
    private String facebookGraphApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public FacebookUserInfo getUserInfoFromAccessToken(String accessToken) {
        try {
            // Gọi Graph API: https://graph.facebook.com/me?access_token=...
            String url = facebookGraphApiUrl + accessToken;
            return restTemplate.getForObject(url, FacebookUserInfo.class);
        } catch (Exception e) {
            return null;
        }
    }

    // Class DTO hứng dữ liệu từ Facebook trả về
    @Data
    public static class FacebookUserInfo {
        private String id;
        private String name;
        private String email;

        @JsonProperty("picture")
        private PictureData picture;

        public String getPictureUrl() {
            if (picture != null && picture.getData() != null) {
                return picture.getData().getUrl();
            }
            return null;
        }

        @Data
        public static class PictureData {
            private DataInternal data;
        }

        @Data
        public static class DataInternal {
            private String url;
        }
    }
}
