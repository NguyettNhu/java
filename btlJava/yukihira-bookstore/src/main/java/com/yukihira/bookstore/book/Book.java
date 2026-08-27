package com.yukihira.bookstore.book;

import com.yukihira.bookstore.author.Author;
import com.yukihira.bookstore.category.Category;
import com.yukihira.bookstore.common.domain.BaseEntity;
import com.yukihira.bookstore.publisher.Publisher;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "books", uniqueConstraints = {
        @UniqueConstraint(name = "uk_books_slug", columnNames = "slug"),
        @UniqueConstraint(name = "uk_books_isbn", columnNames = "isbn")
}, indexes = {
        @Index(name = "idx_books_title", columnList = "title"),
        @Index(name = "idx_books_category", columnList = "category_id"),
        @Index(name = "idx_books_status", columnList = "status")
})
public class Book extends BaseEntity {

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 280)
    private String slug;

    @Column(length = 20)
    private String isbn;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private int stock;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookStatus status = BookStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    @ManyToMany
    @JoinTable(name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id"))
    private Set<Author> authors = new LinkedHashSet<>();

    @Version
    @Column(nullable = false)
    private long version;

    protected Book() {
    }

    public Book(String title, String slug, BigDecimal price, int stock, Category category) {
        this.title = title;
        this.slug = slug;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0 || quantity > stock) {
            throw new IllegalArgumentException("Số lượng tồn kho không đủ");
        }
        stock -= quantity;
        if (stock == 0) status = BookStatus.OUT_OF_STOCK;
    }

    public void increaseStock(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Số lượng hoàn kho phải lớn hơn 0");
        stock += quantity;
        if (status == BookStatus.OUT_OF_STOCK) status = BookStatus.ACTIVE;
    }

    public String getTitle() { return title; }
    public String getSlug() { return slug; }
    public String getIsbn() { return isbn; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public int getStock() { return stock; }
    public String getImageUrl() { return imageUrl; }
    public BookStatus getStatus() { return status; }
    public Category getCategory() { return category; }
    public Publisher getPublisher() { return publisher; }
    public Set<Author> getAuthors() { return authors; }
    public long getVersion() { return version; }
    public void setTitle(String title) { this.title = title; }
    public void setSlug(String slug) { this.slug = slug; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setStatus(BookStatus status) { this.status = status; }
    public void setCategory(Category category) { this.category = category; }
    public void setPublisher(Publisher publisher) { this.publisher = publisher; }
    public void setAuthors(Set<Author> authors) { this.authors = new LinkedHashSet<>(authors); }
}
