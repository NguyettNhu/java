package com.yukihira.bookstore.admin.catalog;

public enum ReferenceType {
    CATEGORIES("categories", "Thể loại", "Mô tả"),
    AUTHORS("authors", "Tác giả", "Tiểu sử"),
    PUBLISHERS("publishers", "Nhà xuất bản", "Địa chỉ");

    private final String path;
    private final String label;
    private final String detailsLabel;

    ReferenceType(String path, String label, String detailsLabel) {
        this.path = path;
        this.label = label;
        this.detailsLabel = detailsLabel;
    }

    public static ReferenceType fromPath(String path) {
        for (ReferenceType type : values()) {
            if (type.path.equals(path)) return type;
        }
        throw new IllegalArgumentException("Loại dữ liệu không hợp lệ");
    }

    public String getPath() { return path; }
    public String getLabel() { return label; }
    public String getDetailsLabel() { return detailsLabel; }
}
