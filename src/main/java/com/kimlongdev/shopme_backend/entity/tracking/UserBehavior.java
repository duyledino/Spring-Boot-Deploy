package com.kimlongdev.shopme_backend.entity.tracking;

import com.fasterxml.jackson.databind.JsonNode;
import com.kimlongdev.shopme_backend.entity.product.Category;
import com.kimlongdev.shopme_backend.entity.product.Product;
import com.kimlongdev.shopme_backend.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_behaviors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBehavior {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long behaviorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "session_id")
    private String sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "action_type", nullable = false)
    private String actionType; // VIEW, CLICK, SEARCH, ADD_CART

    private String keyword;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "context_data", columnDefinition = "jsonb")
    private JsonNode contextData; // {"device": "mobile", "duration": 50}

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}