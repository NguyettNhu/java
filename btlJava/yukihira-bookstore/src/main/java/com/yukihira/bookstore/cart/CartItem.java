package com.yukihira.bookstore.cart;

import com.yukihira.bookstore.book.Book;
import com.yukihira.bookstore.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "cart_items", uniqueConstraints =
        @UniqueConstraint(name = "uk_cart_items_cart_book", columnNames = {"cart_id", "book_id"}))
public class CartItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private int quantity;

    protected CartItem() {
    }

    public CartItem(Cart cart, Book book, int quantity) {
        this.cart = cart;
        this.book = book;
        setQuantity(quantity);
    }

    public Cart getCart() { return cart; }
    public Book getBook() { return book; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        this.quantity = quantity;
    }
}
