package com.kimlongdev.shopme_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kimlongdev.shopme_backend.entity.user.User;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    @JsonProperty("access_token")
    private String accessToken;

    private UserInfo user;

    // ========================================================================
    // INNER CLASSES (Dùng static để không phụ thuộc instance cha)
    // ========================================================================

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        @JsonProperty("full_name")
        private String fullName;
        private String email;
        private String role;
        private String avatar;

        public static UserInfo fromEntity(User entity) {
            return UserInfo.builder()
                    .id(entity.getUserId())
                    .fullName(entity.getFullName())
                    .email(entity.getEmail())
                    .role(entity.getRole())
                    .avatar(entity.getAvatar())
                    .build();
        }
    }

    /* Class này dùng cho API "/auth/account" (lấy thông tin user hiện tại).
       Thường FE sẽ gọi API này sau khi F5 trang để lấy lại thông tin từ Token.
    */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserGetAccount {
        private UserInfo user;
    }

    /*
       Class này dùng để hứng dữ liệu khi decode JWT (Token chứa gì thì map vào đây).
       Thường dùng trong SecurityFilter hoặc JwtUtil.
    */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInsideToken {
        private Long id;
        private String email;
        private String fullName;
        private String role;
    }
}
