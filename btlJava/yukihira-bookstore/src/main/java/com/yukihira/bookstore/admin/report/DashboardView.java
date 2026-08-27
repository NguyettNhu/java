package com.yukihira.bookstore.admin.report;

import java.math.BigDecimal;
import java.util.List;

public record DashboardView(
        long totalOrders,
        long pendingOrders,
        BigDecimal completedRevenue,
        long customerCount,
        long activeBookCount,
        List<TopSellingBookView> topSellingBooks
) {
}
