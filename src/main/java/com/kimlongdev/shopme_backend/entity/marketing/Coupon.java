package com.kimlongdev.shopme_backend.entity.marketing;

import com.kimlongdev.shopme_backend.entity.seller.Seller;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "discount_type", nullable = false)
    private String discountType; // PERCENT, FIXED

    @Column(name = "discount_value", nullable = false)
    private BigDecimal discountValue;

    @Column(name = "min_order_amount")
    private BigDecimal minOrderAmount;

    @Column(name = "max_usage")
    private Integer maxUsage;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller; // Nullable (System coupon or Seller coupon)
}
