package com.kimlongdev.shopme_backend.util.Enum;

public enum TransactionType {
    DEPOSIT,    // Nạp tiền vào ví
    WITHDRAW,   // Rút tiền về ngân hàng
    PAYMENT,    // Thanh toán đơn hàng
    REFUND,     // Nhận hoàn tiền từ đơn hủy
    COMMISSION  // Phí sàn (thu từ Seller)
}
