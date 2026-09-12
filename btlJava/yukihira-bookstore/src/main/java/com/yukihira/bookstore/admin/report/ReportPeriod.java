package com.yukihira.bookstore.admin.report;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public record ReportPeriod(LocalDate from, LocalDate to) {
    public static final ZoneId ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    public ReportPeriod {
        if (from == null || to == null || from.isAfter(to)) {
            throw new IllegalArgumentException("Ngày bắt đầu phải trước hoặc bằng ngày kết thúc.");
        }
    }
    public Instant start() { return from.atStartOfDay(ZONE).toInstant(); }
    public Instant endExclusive() { return to.plusDays(1).atStartOfDay(ZONE).toInstant(); }
}
