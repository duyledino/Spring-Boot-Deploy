package com.kimlongdev.shopme_backend.entity.user;

import com.kimlongdev.shopme_backend.util.Enum.USER_ROLE;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.List;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_lookup", columnList = "email, mobile")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "full_name", nullable = false)
    @Builder.Default
    private String fullName = "Khách hàng";

    @Column(unique = true)
    private String email;

    @Column(unique = true, length = 15)
    private String mobile;

    private String password;
    private String avatar;

    @Column(nullable = false)
    @Builder.Default
    private String role = String.valueOf(USER_ROLE.ROLE_CUSTOMER);

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private java.time.LocalDateTime createdAt;

    // Relationships
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Address> addresses;
}