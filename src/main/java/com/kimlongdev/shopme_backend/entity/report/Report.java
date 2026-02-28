package com.kimlongdev.shopme_backend.entity.report;

import com.fasterxml.jackson.databind.JsonNode;
import com.kimlongdev.shopme_backend.entity.common.BaseEntity;
import com.kimlongdev.shopme_backend.entity.product.Product;
import com.kimlongdev.shopme_backend.entity.seller.Seller;
import com.kimlongdev.shopme_backend.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false, length = 50)
    private String reason;

    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "evidence_images", columnDefinition = "jsonb")
    private JsonNode evidenceImages;

    @Builder.Default
    private String status = "PENDING";

    @Column(name = "admin_note")
    private String adminNote;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;
}