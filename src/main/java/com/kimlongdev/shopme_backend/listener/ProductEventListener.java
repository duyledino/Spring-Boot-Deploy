package com.kimlongdev.shopme_backend.listener;

import com.kimlongdev.shopme_backend.document.ProductDocument;
import com.kimlongdev.shopme_backend.event.ProductEvent;
import com.kimlongdev.shopme_backend.mapper.ProductDocumentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventListener {

    //private final ProductElasticsearchRepository esRepository;
    private final ProductDocumentMapper documentMapper;

    /**
     * @Async: Chạy ở một thread riêng biệt, không làm user phải chờ.
     * @TransactionalEventListener: Chỉ chạy khi Transaction DB chính đã COMMIT thành công.
     * (Tránh trường hợp lưu DB lỗi rollback mà vẫn lưu vào ES thì sai dữ liệu).
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductSavedEvent(ProductEvent event) {
        log.info(">>> Bắt đầu đồng bộ Product ID={} sang Elasticsearch...", event.getProduct().getProductId());

        try {
            // 1. Convert Entity -> Document (Dùng MapStruct)
            ProductDocument doc = documentMapper.toDocument(event.getProduct());

            // 2. Lưu vào Elasticsearch
            //esRepository.save(doc);

            log.info(">>> Đồng bộ thành công Product ID={}!", event.getProduct().getProductId());
        } catch (Exception e) {
            // Lưu ý: Vì chạy async nên nếu lỗi, user sẽ không biết.
            // Cần log kỹ để dev check hoặc bắn vào bảng log lỗi để retry sau.
            log.error(">>> Lỗi đồng bộ ES cho Product ID={}: {}", event.getProduct().getProductId(), e.getMessage());
        }
    }
}
