package com.yukihira.bookstore.admin.report;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public record AnalyticsView(AnalyticsFilter filter, long totalBooks, long totalCategories, long stock,
                            long lowStock, long emptyStock, BigDecimal stockValue, long quantitySold,
                            long totalOrders, long completedOrders, BigDecimal revenue, long buyers,
                            List<Chart> charts, List<ReferenceStats> references) {
    public BigDecimal averageOrderValue() {
        return completedOrders == 0 ? BigDecimal.ZERO : revenue.divide(BigDecimal.valueOf(completedOrders), 2, RoundingMode.HALF_UP);
    }
    public record Point(String label, BigDecimal value, String url) {}
    public record Chart(String title, String type, String unit, String note, List<Point> points) {}
    public record ReferenceStats(Long id, String name, String details, boolean active,
                                 long bookCount, long stock, long sold, BigDecimal revenue) {}
}
