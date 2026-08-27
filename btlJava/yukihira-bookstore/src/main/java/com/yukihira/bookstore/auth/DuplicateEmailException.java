package com.yukihira.bookstore.auth;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException() {
        super("Email đã được sử dụng");
    }
}
