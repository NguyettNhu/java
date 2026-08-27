package com.yukihira.bookstore.order;

final class OrderLabels {

    private OrderLabels() {
    }

    static String status(OrderStatus status) {
        return status.getLabel();
    }

    static String paymentMethod(PaymentMethod method) {
        return method.getLabel();
    }

    static String paymentStatus(PaymentStatus status) {
        return status.getLabel();
    }
}
