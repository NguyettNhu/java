package com.yukihira.bookstore.book;

import java.math.BigDecimal;

public record BookSearchQuery(String keyword, Long categoryId, Long authorId,
                              BigDecimal minPrice, BigDecimal maxPrice, String sort) {
}
