package com.yukihira.bookstore.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class StockForm {
    @NotNull(message = "Thiếu số lượng tồn kho")
    @Min(value = 0, message = "Tồn kho không được âm")
    private Integer stock;
    @NotNull(message = "Hãy tải lại trang để lấy phiên bản tồn kho mới nhất")
    private Long version;

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
