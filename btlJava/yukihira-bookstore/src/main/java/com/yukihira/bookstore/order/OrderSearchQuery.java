package com.yukihira.bookstore.order;

import java.time.LocalDate;

public record OrderSearchQuery(String keyword, OrderStatus status, LocalDate from, LocalDate to) {
    public OrderSearchQuery(String keyword, OrderStatus status) {
        this(keyword, status, null, null);
    }

    public OrderSearchQuery {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("Ngày bắt đầu phải trước hoặc bằng ngày kết thúc.");
        }
    }
}
