package com.kimlongdev.shopme_backend.mapper;

import com.kimlongdev.shopme_backend.mapper.config.BaseMapperConfig;
import org.mapstruct.Mapper;

@Mapper(
        config = BaseMapperConfig.class
        //uses = {OrderItemMapper.class, UserMapper.class} // 🔥 QUAN TRỌNG: Khai báo các Mapper con cần dùng
)
public interface OrderMapper {

    // 1. Map thông tin User tóm tắt (Nhờ UserMapper lo)
    //@Mapping(source = "user", target = "userInfo")

    // 2. Map danh sách Item (MapStruct tự động tìm OrderItemMapper để loop qua list)
    //@Mapping(source = "orderItems", target = "items")
    //OrderResponse toResponse(Order order);

    // 🔥 KỸ THUẬT CAO CẤP: @AfterMapping
    // Chạy sau khi map xong hết các field. Dùng để tính toán logic động.
//    @AfterMapping
//    default void calculateFinalStatus(@MappingTarget OrderResponse response, Order order) {
//        // Ví dụ: Logic hiển thị trạng thái tiếng Việt
//        if ("DELIVERED".equals(order.getStatus())) {
//            response.setStatusLabel("Giao thành công - Cảm ơn bạn!");
//        } else if ("CANCELLED".equals(order.getStatus())) {
//            response.setStatusLabel("Đã hủy");
//        } else {
//            response.setStatusLabel("Đang xử lý");
//        }
//
//        // Ví dụ: Tính tổng số lượng sản phẩm (nếu DB không lưu)
//        int totalQty = response.getItems().stream()
//                .mapToInt(item -> item.getQuantity())
//                .sum();
//        response.setTotalQuantityDisplay(totalQty);
//    }
}
