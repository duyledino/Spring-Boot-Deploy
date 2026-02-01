package com.kimlongdev.shopme_backend.entity.seller;

import com.kimlongdev.shopme_backend.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payout_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayoutRequest extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Builder.Default
    @Column(length = 20)
    private String status = "PENDING"; // ENUM: PENDING, APPROVED, REJECTED

    @Column(name = "admin_note")
    private String adminNote;

    @Column(name = "bank_snapshot")
    private String bankSnapshot;
}