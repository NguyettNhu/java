package com.yukihira.bookstore.user;

public record UserSearchQuery(String keyword, UserStatus status) {
}
