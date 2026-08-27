package com.yukihira.bookstore.user;

public enum UserStatus {
    ACTIVE("Đang hoạt động"),
    LOCKED("Đã khóa");

    private final String label;

    UserStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
