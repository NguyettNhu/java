package com.yukihira.bookstore.admin.report;

import java.util.Set;
import java.util.regex.Pattern;

/** Xác định trang admin nào có khối phân tích và phạm vi dữ liệu mà trang đó cần tổng hợp. */
public record AnalyticsScope(String path, String section, Long detailId) {

    private static final Pattern SUPPORTED = Pattern.compile(
            "/admin(?:/(books|users|orders)(/\\d+)?|/(reports|inventory|categories|authors|publishers))?");
    private static final Set<String> CATALOG_SECTIONS =
            Set.of("dashboard", "reports", "books", "inventory", "categories", "publishers", "authors");
    /** Ba trang này dựng bảng quản lý từ chính số liệu phân tích nên không thể tải sau. */
    private static final Set<String> INLINE_SECTIONS = Set.of("categories", "authors", "publishers");

    /** Trả về null khi đường dẫn không phải trang admin có báo cáo, kể cả khi do client tự gửi lên. */
    public static AnalyticsScope of(String path) {
        if (path == null || !SUPPORTED.matcher(path).matches()) return null;
        String[] parts = path.split("/");
        return new AnalyticsScope(path, parts.length > 2 ? parts[2] : "dashboard",
                parts.length > 3 ? Long.valueOf(parts[3]) : null);
    }

    public boolean detail() {
        return detailId != null;
    }

    public boolean catalog() {
        return CATALOG_SECTIONS.contains(section);
    }

    public boolean inlineAnalytics() {
        return INLINE_SECTIONS.contains(section);
    }
}
