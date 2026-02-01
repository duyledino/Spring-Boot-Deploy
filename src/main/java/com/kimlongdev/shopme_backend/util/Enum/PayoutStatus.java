package com.kimlongdev.shopme_backend.util.Enum;

public enum PayoutStatus {
    PENDING,    // Chờ Admin duyệt
    APPROVED,   // Đã duyệt, tiền đang chuyển
    REJECTED,   // Từ chối (sai stk,...)
    COMPLETED   // Ngân hàng báo thành công
}
