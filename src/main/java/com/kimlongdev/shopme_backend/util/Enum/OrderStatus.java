package com.kimlongdev.shopme_backend.util.Enum;

public enum OrderStatus {
    PENDING,            // Chờ xác nhận
    CONFIRMED,          // Đã xác nhận, đang đóng gói
    SHIPPING,           // Đã giao cho đơn vị vận chuyển
    DELIVERED,          // Giao thành công
    CANCELLED,          // Đã hủy
    RETURNED,           // Trả hàng/Hoàn tiền
    DELIVERY_FAILED     // Giao thất bại
}
