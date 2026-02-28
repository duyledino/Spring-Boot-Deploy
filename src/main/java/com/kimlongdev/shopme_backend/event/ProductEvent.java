package com.kimlongdev.shopme_backend.event;

import com.kimlongdev.shopme_backend.entity.product.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductEvent {
    private Product product; // Dữ liệu sản phẩm vừa lưu xong
    // Bạn có thể thêm Enum TYPE (CREATE, UPDATE, DELETE) nếu muốn xử lý xóa
}
