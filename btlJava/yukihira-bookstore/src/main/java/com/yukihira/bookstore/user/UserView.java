package com.yukihira.bookstore.user;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public record UserView(
        Long id,
        String fullName,
        String email,
        String phone,
        String address,
        UserStatus status,
        Instant createdAt
) {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
            .ofPattern("dd/MM/yyyy").withZone(ZoneId.of("Asia/Ho_Chi_Minh"));

    public String createdAtLabel() { return DATE_FORMAT.format(createdAt); }
}
