package com.yukihira.bookstore.admin.report;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public record AnalyticsView(AnalyticsFilter filter, long totalBooks, long totalCategories, long stock,
                            long lowStock, long emptyStock, BigDecimal stockValue, long quantitySold,
                            long totalOrders, long completedOrders, BigDecimal revenue, long buyers,
                            List<Chart> charts,
                            List<OrderStatusTotal> statusTotals, Comparison previous) {
    public BigDecimal averageOrderValue() {
        return average(revenue, completedOrders);
    }

    /** Tìm biểu đồ theo khóa để trang báo cáo xếp từng biểu đồ vào đúng mục. */
    public Chart chart(String key) {
        return charts.stream().filter(chart -> chart.key().equals(key)).findFirst().orElse(null);
    }

    public AnalyticsView withPrevious(Comparison comparison) {
        return new AnalyticsView(filter, totalBooks, totalCategories, stock, lowStock, emptyStock, stockValue, quantitySold,
                totalOrders, completedOrders, revenue, buyers, charts, statusTotals, comparison);
    }

    /** Tỉ lệ phần trăm số đơn của một trạng thái trên tổng số đơn trong kỳ. */
    public BigDecimal statusShare(OrderStatusTotal row) {
        return totalOrders == 0 ? BigDecimal.ZERO
                : BigDecimal.valueOf(row.count() * 100).divide(BigDecimal.valueOf(totalOrders), 1, RoundingMode.HALF_UP);
    }

    public BigDecimal revenueChange() { return previous == null ? null : change(revenue, previous.revenue()); }
    public BigDecimal ordersChange() { return previous == null ? null : change(BigDecimal.valueOf(totalOrders), BigDecimal.valueOf(previous.totalOrders())); }
    public BigDecimal quantityChange() { return previous == null ? null : change(BigDecimal.valueOf(quantitySold), BigDecimal.valueOf(previous.quantitySold())); }
    public BigDecimal buyersChange() { return previous == null ? null : change(BigDecimal.valueOf(buyers), BigDecimal.valueOf(previous.buyers())); }
    public BigDecimal averageOrderValueChange() { return previous == null ? null : change(averageOrderValue(), previous.averageOrderValue()); }

    /** Trả về null khi kỳ trước bằng 0 vì không có mốc để tính phần trăm. */
    private static BigDecimal change(BigDecimal current, BigDecimal before) {
        if (before.signum() == 0) return null;
        return current.subtract(before).multiply(BigDecimal.valueOf(100)).divide(before, 1, RoundingMode.HALF_UP);
    }

    private static BigDecimal average(BigDecimal total, long count) {
        return count == 0 ? BigDecimal.ZERO : total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }

    public record Point(String label, BigDecimal value, String url) {}
    public record Chart(String key, String title, String type, String unit, String note, List<Point> points) {}
    public record ReferenceStats(Long id, String name, String details, boolean active,
                                 long bookCount, long stock, long sold, BigDecimal revenue) {}
    /** Số liệu tóm tắt của kỳ liền trước, dùng cho cột so sánh trên trang báo cáo. */
    public record Comparison(ReportPeriod range, BigDecimal revenue, long totalOrders, long completedOrders,
                             long quantitySold, long buyers) {
        public BigDecimal averageOrderValue() { return average(revenue, completedOrders); }
    }
}
