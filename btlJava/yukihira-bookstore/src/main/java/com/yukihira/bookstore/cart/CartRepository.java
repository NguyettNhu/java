package com.yukihira.bookstore.cart;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    @EntityGraph(attributePaths = {"items", "items.book"})
    Optional<Cart> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"items", "items.book"})
    Optional<Cart> findByUserEmailIgnoreCase(String email);
}
