package com.yukihira.bookstore.admin.report;

import java.math.BigDecimal;
import java.util.List;

// DTO chứa các số liệu được hiển thị trên dashboard quản trị.
public record DashboardView(
        long totalOrders,
        long pendingOrders,
        BigDecimal completedRevenue,
        long customerCount,
        long activeBookCount,
        List<TopSellingBookView> topSellingBooks
) {
}
