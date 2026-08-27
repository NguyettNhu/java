package com.yukihira.bookstore.order;

public record OrderSearchQuery(String keyword, OrderStatus status) {
}
