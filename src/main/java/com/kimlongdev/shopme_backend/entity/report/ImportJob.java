package com.kimlongdev.shopme_backend.entity.report;

import com.fasterxml.jackson.databind.JsonNode;
import com.kimlongdev.shopme_backend.entity.common.BaseEntity;
import com.kimlongdev.shopme_backend.entity.seller.Seller;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "import_jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportJob extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Builder.Default
    private String status = "PENDING";

    @Column(name = "total_records")
    private Integer totalRecords;

    @Column(name = "success_count")
    private Integer successCount;

    @Column(name = "error_count")
    private Integer errorCount;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "error_log", columnDefinition = "jsonb")
    private JsonNode errorLog;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
