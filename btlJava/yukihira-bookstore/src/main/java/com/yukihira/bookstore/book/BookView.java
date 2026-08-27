package com.yukihira.bookstore.book;

import java.math.BigDecimal;
import java.util.List;

public record BookView(Long id, String title, String slug, String isbn, String description,
                       BigDecimal price, int stock, String imageUrl, BookStatus status,
                       Long categoryId, String categoryName, String publisherName,
                       List<String> authors) {

    public String authorLine() {
        return authors.isEmpty() ? "Yukihira tuyển chọn" : String.join(", ", authors);
    }

    public boolean available() {
        return status == BookStatus.ACTIVE && stock > 0;
    }
}
