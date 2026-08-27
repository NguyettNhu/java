package com.yukihira.bookstore.cart;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartIdAndBookId(Long cartId, Long bookId);
    Optional<CartItem> findByIdAndCartUserEmailIgnoreCase(Long id, String email);
}
