package com.yukihira.bookstore.book;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

public class BookForm {

    private Long id;

    @NotBlank(message = "Vui lòng nhập tên sách")
    @Size(max = 255, message = "Tên sách không quá 255 ký tự")
    private String title;

    @Size(max = 20, message = "ISBN không quá 20 ký tự")
    private String isbn;

    @Size(max = 5000, message = "Mô tả không quá 5000 ký tự")
    private String description;

    @NotNull(message = "Vui lòng nhập giá")
    @DecimalMin(value = "0", message = "Giá không được âm")
    private BigDecimal price;

    @Min(value = 0, message = "Tồn kho không được âm")
    private int stock;

    @Size(max = 500, message = "URL ảnh không quá 500 ký tự")
    private String imageUrl;

    @NotNull(message = "Vui lòng chọn thể loại")
    private Long categoryId;

    private Long publisherId;
    private Set<Long> authorIds = new LinkedHashSet<>();
    private BookStatus status = BookStatus.ACTIVE;

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getIsbn() { return isbn; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public int getStock() { return stock; }
    public String getImageUrl() { return imageUrl; }
    public Long getCategoryId() { return categoryId; }
    public Long getPublisherId() { return publisherId; }
    public Set<Long> getAuthorIds() { return authorIds; }
    public BookStatus getStatus() { return status; }
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public void setPublisherId(Long publisherId) { this.publisherId = publisherId; }
    public void setAuthorIds(Set<Long> authorIds) { this.authorIds = authorIds == null ? new LinkedHashSet<>() : authorIds; }
    public void setStatus(BookStatus status) { this.status = status; }
}
