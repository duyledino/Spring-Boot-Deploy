package com.kimlongdev.shopme_backend.entity.payment;


import com.kimlongdev.shopme_backend.entity.seller.Seller;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@IdClass(TransactionPK.class) // Ánh xạ PK
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @Id
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    // LƯU Ý QUAN TRỌNG:
    // Vì bảng Orders cũng là Partition Table có Composite Key (order_id, created_at)
    // Nhưng bảng Transactions lại KHÔNG lưu order_created_at.
    // -> Không thể map @ManyToOne trực tiếp chuẩn JPA được.
    // -> Map dạng "Logical Reference" (chỉ lưu ID).
    @Column(name = "order_id")
    private Long orderId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 20)
    private String type; // DEPOSIT, WITHDRAW, PAYMENT

    private String description;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
