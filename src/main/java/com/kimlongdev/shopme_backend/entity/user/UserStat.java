package com.kimlongdev.shopme_backend.entity.user;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "user_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStat {
    @Id
    @Column(name = "user_id")
    private Long userId;

    // MapsId giúp share PK với bảng User
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "total_orders")
    private Integer totalOrders;

    @Column(name = "returned_orders_count")
    private Integer returnedOrdersCount;

    @Column(name = "reputation_score")
    private BigDecimal reputationScore;

    @Column(name = "is_restricted")
    private Boolean isRestricted;
}
