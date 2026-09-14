package com.yukihira.bookstore.api;

public record ReferenceResponse(Long id, String name, String slug, String details, Boolean active) {
}
