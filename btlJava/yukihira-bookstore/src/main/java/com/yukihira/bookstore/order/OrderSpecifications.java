package com.yukihira.bookstore.order;

import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

final class OrderSpecifications {

    private OrderSpecifications() {
    }

    static Specification<CustomerOrder> from(OrderSearchQuery query) {
        return Specification.allOf(keywordContains(query.keyword()), hasStatus(query.status()));
    }

    private static Specification<CustomerOrder> keywordContains(String keyword) {
        return (root, criteriaQuery, builder) -> {
            if (keyword == null || keyword.isBlank()) return builder.conjunction();
            String pattern = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
            return builder.or(
                    builder.like(builder.lower(root.get("orderCode").as(String.class)), pattern),
                    builder.like(builder.lower(root.get("receiverName").as(String.class)), pattern),
                    builder.like(builder.lower(root.get("user").get("email").as(String.class)), pattern)
            );
        };
    }

    private static Specification<CustomerOrder> hasStatus(OrderStatus status) {
        return (root, criteriaQuery, builder) -> status == null
                ? builder.conjunction()
                : builder.equal(root.get("status"), status);
    }
}
