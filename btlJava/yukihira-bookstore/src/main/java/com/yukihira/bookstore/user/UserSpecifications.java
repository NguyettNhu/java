package com.yukihira.bookstore.user;

import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

final class UserSpecifications {

    private UserSpecifications() {
    }

    static Specification<User> customers(UserSearchQuery query) {
        return Specification.allOf(hasRole(Role.CUSTOMER), keywordContains(query.keyword()), hasStatus(query.status()));
    }

    private static Specification<User> hasRole(Role role) {
        return (root, criteriaQuery, builder) -> builder.equal(root.get("role"), role);
    }

    private static Specification<User> keywordContains(String keyword) {
        return (root, criteriaQuery, builder) -> {
            if (keyword == null || keyword.isBlank()) return builder.conjunction();
            String pattern = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
            return builder.or(
                    builder.like(builder.lower(root.get("fullName").as(String.class)), pattern),
                    builder.like(builder.lower(root.get("email").as(String.class)), pattern),
                    builder.like(builder.lower(root.get("phone").as(String.class)), pattern)
            );
        };
    }

    private static Specification<User> hasStatus(UserStatus status) {
        return (root, criteriaQuery, builder) -> status == null
                ? builder.conjunction()
                : builder.equal(root.get("status"), status);
    }
}
