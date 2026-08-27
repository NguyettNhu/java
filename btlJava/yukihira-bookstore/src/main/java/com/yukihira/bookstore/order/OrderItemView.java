package com.yukihira.bookstore.order;

import java.math.BigDecimal;

public record OrderItemView(
        Long bookId,
        String title,
        String slug,
        String imageUrl,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal subtotal
) {
}
