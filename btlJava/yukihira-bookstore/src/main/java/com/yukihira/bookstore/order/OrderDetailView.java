package com.yukihira.bookstore.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record OrderDetailView(
        Long id,
        String orderCode,
        String customerName,
        String customerEmail,
        String receiverName,
        String receiverPhone,
        String shippingAddress,
        String note,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        OrderStatus status,
        Instant createdAt,
        List<OrderItemView> items,
        List<OrderStatus> allowedTransitions,
        boolean customerCanCancel
) {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.of("Asia/Ho_Chi_Minh"));

    public String statusLabel() { return OrderLabels.status(status); }
    public String paymentMethodLabel() { return OrderLabels.paymentMethod(paymentMethod); }
    public String paymentStatusLabel() { return OrderLabels.paymentStatus(paymentStatus); }
    public String createdAtLabel() { return DATE_FORMAT.format(createdAt); }
}
