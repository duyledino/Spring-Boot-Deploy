package com.kimlongdev.shopme_backend.entity.product;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long variantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "variant_attributes", columnDefinition = "jsonb")
    private JsonNode variantAttributes; // {"color": "Red", "size": "XL"}

    private String sku;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "image_url")
    private String imageUrl;

    @Builder.Default
    @Column(name = "inventory_status")
    private Boolean inventoryStatus = true;
}