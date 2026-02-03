package com.kimlongdev.shopme_backend.mapper;

import com.kimlongdev.shopme_backend.entity.order.OrderItem;
import com.kimlongdev.shopme_backend.mapper.config.BaseMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapperConfig.class)
public interface OrderItemMapper {

    // Map tên sản phẩm từ quan hệ Product
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.images", target = "productImage", qualifiedByName = "getFirstImageFromItem")
    // Lưu ý: Bạn có thể tái sử dụng logic getFirstImage bằng cách viết vào 1 class Utils riêng
    // nhưng ở đây tôi viết inline để bạn dễ hiểu.
    //OrderItemResponse toResponse(OrderItem item);

    // Ví dụ logic lấy ảnh
    @org.mapstruct.Named("getFirstImageFromItem")
    default String getFirstImageFromItem(java.util.List<com.kimlongdev.shopme_backend.entity.product.ProductImage> images) {
        return null;//(images != null && !images.isEmpty()) ? images.get(0).getUrl() : null;
    }
}