package com.yukihira.bookstore.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public record OrderSummaryView(
        Long id,
        String orderCode,
        String customerName,
        String customerEmail,
        Instant createdAt,
        BigDecimal totalAmount,
        int itemCount,
        OrderStatus status,
        PaymentStatus paymentStatus
) {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.of("Asia/Ho_Chi_Minh"));

    public String statusLabel() {
        return OrderLabels.status(status);
    }

    public String paymentStatusLabel() {
        return OrderLabels.paymentStatus(paymentStatus);
    }

    public String createdAtLabel() {
        return DATE_FORMAT.format(createdAt);
    }
}
