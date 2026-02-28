package com.kimlongdev.shopme_backend.entity.marketing;

import com.kimlongdev.shopme_backend.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupon_usages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    // Logical Reference tới bảng Partition Orders
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "used_at")
    private LocalDateTime usedAt;
}