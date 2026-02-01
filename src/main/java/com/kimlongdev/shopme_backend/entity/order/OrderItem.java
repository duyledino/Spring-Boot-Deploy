package com.kimlongdev.shopme_backend.entity.order;

import com.fasterxml.jackson.databind.JsonNode;
import com.kimlongdev.shopme_backend.entity.product.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    // Map tới Order thông qua cột order_id
    // Lưu ý: Vì Order dùng Composite Key (order_id, created_at),
    // JoinColumn thông thường chỉ trỏ tới order_id sẽ gây warning hoặc lỗi nếu không cấu hình kỹ.
    // Ở đây ta giả định logic ứng dụng query qua order_id là chính.
    @JoinColumns({
            @JoinColumn(name = "order_id", referencedColumnName = "order_id")
            // referencedColumnName = "created_at" KHÔNG THỂ MAP vì order_items không có created_at
            // JPA Workaround: Coi Order như entity thường khi join, hoặc dùng @NotFound
    })
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;

    @Column(name = "price_at_purchase")
    private BigDecimal priceAtPurchase;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "variant_info", columnDefinition = "jsonb")
    private JsonNode variantInfo;
}
