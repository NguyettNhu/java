package com.yukihira.bookstore.admin.report;

import com.yukihira.bookstore.order.OrderStatus;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public record ReportView(ReportPeriod period, List<OrderStatusTotal> statusTotals,
                         List<TopSellingBookView> topSellingBooks) {
    public long totalOrders() { return statusTotals.stream().mapToLong(OrderStatusTotal::count).sum(); }
    public long completedOrders() {
        return statusTotals.stream().filter(row -> row.status() == OrderStatus.COMPLETED)
                .mapToLong(OrderStatusTotal::count).sum();
    }
    public BigDecimal completedRevenue() {
        return statusTotals.stream().filter(row -> row.status() == OrderStatus.COMPLETED)
                .map(OrderStatusTotal::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public BigDecimal averageOrderValue() {
        return completedOrders() == 0 ? BigDecimal.ZERO
                : completedRevenue().divide(BigDecimal.valueOf(completedOrders()), 2, RoundingMode.HALF_UP);
    }
}
