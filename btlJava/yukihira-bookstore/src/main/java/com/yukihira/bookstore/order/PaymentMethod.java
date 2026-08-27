package com.yukihira.bookstore.order;

public enum PaymentMethod {
    COD("Thanh toán khi nhận hàng"),
    BANK_TRANSFER("Chuyển khoản ngân hàng");

    private final String label;

    PaymentMethod(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
