package com.kimlongdev.shopme_backend.mapper;

import com.kimlongdev.shopme_backend.entity.product.Product;
import com.kimlongdev.shopme_backend.entity.product.ProductImage;
import com.kimlongdev.shopme_backend.mapper.config.BaseMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(config = BaseMapperConfig.class)
public interface ProductMapper {

    // 1. Flattening: Lấy tên Category từ object con ra ngoài
    @Mapping(source = "category.name", target = "categoryName")

    // 2. Custom Logic: Lấy ảnh đầu tiên làm thumbnail
    @Mapping(source = "images", target = "thumbnail", qualifiedByName = "getFirstImage")
    //ProductResponse toResponse(Product product);

    // --- CÁC HÀM XỬ LÝ LOGIC PHỨC TẠP ---

    @Named("getFirstImage")
    default String getFirstImage(List<ProductImage> images) {
        if (images == null || images.isEmpty()) {
            return "default-placeholder.png"; // Ảnh mặc định nếu không có
        }
        // Giả sử ProductImage có hàm getUrl()
        return null;//images.get(0).getUrl();
    }
}