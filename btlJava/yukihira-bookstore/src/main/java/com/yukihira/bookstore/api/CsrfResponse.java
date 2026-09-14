package com.yukihira.bookstore.api;

public record CsrfResponse(String headerName, String parameterName, String token) {
}
