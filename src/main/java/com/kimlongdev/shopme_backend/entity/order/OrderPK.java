package com.kimlongdev.shopme_backend.entity.order;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class OrderPK implements Serializable {
    // Composite Key for Partition Table
    private Long orderId;
    private LocalDateTime createdAt;
}
