package com.kimlongdev.shopme_backend.entity.cms;

import com.kimlongdev.shopme_backend.entity.product.Category;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "home_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long homeCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "display_title", nullable = false)
    private String displayTitle;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "banner_image")
    private String bannerImage;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
}