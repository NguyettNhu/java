package com.yukihira.bookstore.cart;

import java.math.BigDecimal;
import java.util.List;

public record CartView(List<CartItemView> items, BigDecimal total) {
    public int itemCount() {
        return items.stream().mapToInt(CartItemView::quantity).sum();
    }
}
