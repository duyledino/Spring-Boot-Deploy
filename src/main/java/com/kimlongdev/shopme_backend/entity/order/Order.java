package com.kimlongdev.shopme_backend.entity.order;

import com.kimlongdev.shopme_backend.entity.seller.Seller;
import com.kimlongdev.shopme_backend.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_order_user", columnList = "user_id"),
        @Index(name = "idx_order_created", columnList = "created_at")
})
@IdClass(OrderPK.class) // Ánh xạ Composite Key do Partitioning
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Id
    @CreationTimestamp
    @Column(name = "created_at", insertable = false, updatable = false)
    // insertable=false vì DB tự generate timestamp, nhưng cần map để JPA biết là PK
    private LocalDateTime createdAt;

    @Column(name = "order_code", nullable = false)
    private String orderCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    // Shipping Info
    @Column(name = "shipping_name") private String shippingName;
    @Column(name = "shipping_phone") private String shippingPhone;
    @Column(name = "shipping_address") private String shippingAddress;

    // Money
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "status")
    private String status;

    // Relationships
    // Lưu ý: OrderItem cần join qua orderId, JPA 6 thông minh có thể xử lý partial join
    // nhưng tốt nhất OrderItem nên giữ tham chiếu Order
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderItem> items;
}
