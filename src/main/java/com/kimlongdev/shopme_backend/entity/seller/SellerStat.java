package com.kimlongdev.shopme_backend.entity.seller;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seller_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerStat {
    @Id
    @Column(name = "seller_id")
    private Long sellerId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @Column(name = "total_orders")
    private Integer totalOrders;

    @Column(name = "cancelled_by_seller_count")
    private Integer cancelledBySellerCount;

    @Column(name = "warning_level")
    private Integer warningLevel;
}