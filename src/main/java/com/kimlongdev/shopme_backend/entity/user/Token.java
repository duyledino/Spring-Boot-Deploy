package com.kimlongdev.shopme_backend.entity.user;

import com.kimlongdev.shopme_backend.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "refresh_token", nullable = false, length = 500)
    private String refreshToken;

    @Builder.Default
    private Boolean revoked = false;

    @Builder.Default
    private Boolean expired = false;
}