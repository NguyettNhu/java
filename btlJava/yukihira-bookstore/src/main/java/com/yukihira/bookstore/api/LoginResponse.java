package com.yukihira.bookstore.api;

import com.yukihira.bookstore.user.Role;

public record LoginResponse(Long id, String fullName, String email, Role role,
                            String authenticationType) {
}
