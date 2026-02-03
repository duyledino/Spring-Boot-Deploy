package com.kimlongdev.shopme_backend.service.impl;

import com.kimlongdev.shopme_backend.entity.product.Product;
import com.kimlongdev.shopme_backend.event.ProductEvent;
import com.kimlongdev.shopme_backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

//    private final ProductRepository productRepository;
//    private final ApplicationEventPublisher eventPublisher;
//
//    @Override
//    @Transactional
//    public ProductResponse createProduct(ProductRequest request) {
//        // ... Logic map request sang entity ...
//        Product product = ...;
//
//        // 1. Lưu vào Postgres (Transaction chính)
//        Product savedProduct = productRepository.save(product);
//
//        // 2. Bắn sự kiện (Nhanh gọn, không chờ)
//        // Listener sẽ bắt lấy cái này và chạy ngầm
//        eventPublisher.publishEvent(new ProductEvent(savedProduct));
//
//        // 3. Trả về kết quả ngay cho User
//        return productMapper.toResponse(savedProduct);
//    }
}
