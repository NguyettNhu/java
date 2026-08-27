package com.yukihira.bookstore.order;

import com.yukihira.bookstore.book.Book;
import com.yukihira.bookstore.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private CustomerOrder order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "book_title", nullable = false, length = 255)
    private String bookTitle;

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotal;

    protected OrderItem() {
    }

    public OrderItem(CustomerOrder order, Book book, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        this.order = order;
        this.book = book;
        this.bookTitle = book.getTitle();
        this.unitPrice = book.getPrice();
        this.quantity = quantity;
        this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public CustomerOrder getOrder() { return order; }
    public Book getBook() { return book; }
    public String getBookTitle() { return bookTitle; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
    public BigDecimal getSubtotal() { return subtotal; }
}
