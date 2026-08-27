package com.yukihira.bookstore.cart;

import java.math.BigDecimal;

public record CartItemView(Long id, Long bookId, String title, String slug, String imageUrl,
                           BigDecimal unitPrice, int quantity, int availableStock,
                           BigDecimal subtotal) {
}
