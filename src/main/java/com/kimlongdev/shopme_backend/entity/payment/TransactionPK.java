package com.kimlongdev.shopme_backend.entity.payment;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

// 1. Composite PK Class
@Data
@NoArgsConstructor
@AllArgsConstructor
class TransactionPK implements Serializable {
    private Long transactionId;
    private LocalDateTime createdAt;
}
