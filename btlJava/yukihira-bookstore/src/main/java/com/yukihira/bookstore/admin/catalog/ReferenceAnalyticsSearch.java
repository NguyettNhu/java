package com.yukihira.bookstore.admin.catalog;

import com.yukihira.bookstore.admin.report.AnalyticsView.ReferenceStats;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class ReferenceAnalyticsSearch {
    private ReferenceAnalyticsSearch() {}
    public static Page<ReferenceStats> search(List<ReferenceStats> rows, String keyword, String details, int minBooks,
                                              String activity, String availability, String visibility, String sort, int page) {
        return page(filter(rows, keyword, details, minBooks, activity, availability, visibility, sort), page);
    }

    /** Lọc và sắp xếp toàn bộ danh sách, để phần tóm tắt tính trên đúng kết quả đang lọc. */
    public static List<ReferenceStats> filter(List<ReferenceStats> rows, String keyword, String details, int minBooks,
                                              String activity, String availability, String visibility, String sort) {
        Comparator<ReferenceStats> comparator = switch (sort) {
            case "books" -> Comparator.comparingLong(ReferenceStats::bookCount).reversed();
            case "stock" -> Comparator.comparingLong(ReferenceStats::stock).reversed();
            case "sold" -> Comparator.comparingLong(ReferenceStats::sold).reversed();
            case "revenue" -> Comparator.comparing(ReferenceStats::revenue).reversed();
            default -> Comparator.comparing(ReferenceStats::name, String.CASE_INSENSITIVE_ORDER);
        };
        return rows.stream().filter(r -> contains(r.name(), keyword) && contains(r.details(), details))
                .filter(r -> r.bookCount() >= Math.max(0, minBooks))
                .filter(r -> !"selling".equals(activity) || r.sold() > 0)
                .filter(r -> !"unsold".equals(activity) || r.sold() == 0)
                .filter(r -> !"empty".equals(availability) || r.stock() == 0)
                .filter(r -> !"available".equals(availability) || r.stock() > 0)
                .filter(r -> !"active".equals(visibility) || r.active())
                .filter(r -> !"hidden".equals(visibility) || !r.active())
                .sorted(comparator.thenComparing(ReferenceStats::id)).toList();
    }

    public static Page<ReferenceStats> page(List<ReferenceStats> result, int page) {
        var pageable = PageRequest.of(Math.min(Math.max(0, page), Math.max(0, (result.size() - 1) / 20)), 20);
        int start = (int) pageable.getOffset();
        return new PageImpl<>(result.subList(start, Math.min(start + 20, result.size())), pageable, result.size());
    }
    private static boolean contains(String value, String keyword) {
        return keyword == null || keyword.isBlank() || value != null && value.toLowerCase(Locale.ROOT).contains(keyword.trim().toLowerCase(Locale.ROOT));
    }
}
