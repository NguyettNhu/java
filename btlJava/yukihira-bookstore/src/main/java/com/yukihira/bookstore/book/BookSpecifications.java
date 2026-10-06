package com.yukihira.bookstore.book;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public final class BookSpecifications {

    private BookSpecifications() {
    }

    /** Bộ lọc của trang quản lý sách: thêm trạng thái và mức tồn kho ("low" hoặc "empty"). */
    public static Specification<Book> admin(BookSearchQuery filter, BookStatus status, String stock) {
        var spec = from(filter, false);
        if (status != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        if ("low".equals(stock)) spec = spec.and((root, query, cb) -> cb.and(
                cb.between(root.get("stock"), 1, 5), cb.notEqual(root.get("status"), BookStatus.INACTIVE)));
        if ("empty".equals(stock)) spec = spec.and((root, query, cb) -> cb.equal(root.get("stock"), 0));
        return spec;
    }

    public static Specification<Book> from(BookSearchQuery filter, boolean activeOnly) {
        Specification<Book> spec = (root, query, cb) -> cb.conjunction();
        if (activeOnly) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), BookStatus.ACTIVE));
        }
        if (filter.keyword() != null && !filter.keyword().isBlank()) {
            String pattern = "%" + filter.keyword().trim().toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((root, query, cb) -> {
                query.distinct(true);
                return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("isbn")), pattern),
                    cb.like(cb.lower(root.join("authors", JoinType.LEFT).get("name")), pattern));
            });
        }
        if (filter.categoryId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category").get("id"), filter.categoryId()));
        }
        if (filter.authorId() != null) {
            spec = spec.and((root, query, cb) -> {
                query.distinct(true);
                return cb.equal(root.join("authors").get("id"), filter.authorId());
            });
        }
        if (filter.publisherId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("publisher").get("id"), filter.publisherId()));
        }
        if (filter.minPrice() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), filter.minPrice()));
        }
        if (filter.maxPrice() != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), filter.maxPrice()));
        }
        return spec;
    }
}
