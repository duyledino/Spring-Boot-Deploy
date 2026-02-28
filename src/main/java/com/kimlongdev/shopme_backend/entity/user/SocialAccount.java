package com.kimlongdev.shopme_backend.entity.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "social_accounts", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"provider", "provider_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long socialId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String provider; // FACEBOOK, GOOGLE

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    private String email;
    private String name;
}
