package com.yukihira.bookstore.admin.catalog;

import com.yukihira.bookstore.admin.report.AnalyticsView.ReferenceStats;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/**
 * Tóm tắt các dòng thể loại, tác giả hoặc nhà xuất bản đang khớp bộ lọc của trang.
 * Không cộng doanh thu giữa các dòng vì sách nhiều tác giả được tính cho từng tác giả.
 */
public record ReferenceSummary(long total, long hidden, long selling, long withoutBooks,
                               ReferenceStats topRevenue, ReferenceStats mostBooks) {

    public static ReferenceSummary of(List<ReferenceStats> rows) {
        ReferenceStats topRevenue = rows.stream().filter(r -> r.revenue().compareTo(BigDecimal.ZERO) > 0)
                .max(Comparator.comparing(ReferenceStats::revenue).thenComparing(ReferenceStats::id, Comparator.reverseOrder()))
                .orElse(null);
        ReferenceStats mostBooks = rows.stream().filter(r -> r.bookCount() > 0)
                .max(Comparator.comparingLong(ReferenceStats::bookCount).thenComparing(ReferenceStats::id, Comparator.reverseOrder()))
                .orElse(null);
        return new ReferenceSummary(rows.size(), rows.stream().filter(r -> !r.active()).count(),
                rows.stream().filter(r -> r.sold() > 0).count(), rows.stream().filter(r -> r.bookCount() == 0).count(),
                topRevenue, mostBooks);
    }

    public long unsold() {
        return total - selling;
    }
}
