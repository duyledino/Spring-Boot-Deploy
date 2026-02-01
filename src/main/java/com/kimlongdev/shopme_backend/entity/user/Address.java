package com.kimlongdev.shopme_backend.entity.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Column(nullable = false, length = 15)
    private String phone;

    @Column(name = "province_id", nullable = false)
    private Integer provinceId;

    @Column(name = "district_id", nullable = false)
    private Integer districtId;

    @Column(name = "ward_code", nullable = false)
    private String wardCode;

    @Column(name = "address_detail", nullable = false)
    private String addressDetail;

    @Column(name = "full_address", nullable = false, length = 500)
    private String fullAddress;

    @Column(name = "is_default")
    private Boolean isDefault;
}
