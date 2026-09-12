package com.yukihira.bookstore.book;

public enum BookStatus {
    ACTIVE("Đang bán"), INACTIVE("Ngừng bán"), OUT_OF_STOCK("Hết hàng");
    private final String label;
    BookStatus(String label) { this.label = label; }
    public String getLabel() { return label; }
}
