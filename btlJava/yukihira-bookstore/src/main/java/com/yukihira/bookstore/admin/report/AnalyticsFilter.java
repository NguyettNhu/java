package com.yukihira.bookstore.admin.report;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import java.util.Set;

/** Calendar periods are resolved once, in Vietnamese local time, for every chart. */
public record AnalyticsFilter(String period, LocalDate date, ReportPeriod range, String groupBy,
                              Long publisherId, Long categoryId, Long authorId) {
    public static AnalyticsFilter resolve(String period, LocalDate date, LocalDate from, LocalDate to,
                                          String groupBy, Long publisher, Long category, Long author) {
        LocalDate anchor = date == null ? LocalDate.now(ReportPeriod.ZONE) : date;
        String selected = period == null || period.isBlank() ? (from != null || to != null ? "custom" : "month") : period;
        LocalDate start;
        LocalDate end;
        switch (selected) {
            case "day" -> { start = anchor; end = anchor; }
            case "week" -> { start = anchor.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)); end = start.plusDays(6); }
            case "month" -> { start = anchor.withDayOfMonth(1); end = start.with(TemporalAdjusters.lastDayOfMonth()); }
            case "quarter" -> { start = LocalDate.of(anchor.getYear(), ((anchor.getMonthValue() - 1) / 3) * 3 + 1, 1); end = start.plusMonths(3).minusDays(1); }
            case "year" -> { start = anchor.withDayOfYear(1); end = start.plusYears(1).minusDays(1); }
            case "custom" -> {
                if (from == null || to == null) throw new IllegalArgumentException("Hãy nhập đủ ngày bắt đầu và ngày kết thúc.");
                start = from; end = to;
            }
            default -> throw new IllegalArgumentException("Kỳ thống kê không hợp lệ.");
        }
        // Giới hạn khoảng ngày để phép tính lịch an toàn và kết quả không quá lớn.
        if (start.getYear() < 1900 || end.getYear() > 9998 || end.toEpochDay() - start.toEpochDay() > 3660)
            throw new IllegalArgumentException("Chọn khoảng thống kê từ năm 1900 và không quá 10 năm.");
        String grouping = groupBy == null || groupBy.isBlank() ? (Set.of("year", "quarter").contains(selected) ? "month" : "day") : groupBy;
        if (!Set.of("day", "week", "month", "quarter", "year").contains(grouping))
            throw new IllegalArgumentException("Cách nhóm thời gian không hợp lệ.");
        return new AnalyticsFilter(selected, anchor, new ReportPeriod(start, end), grouping, publisher, category, author);
    }

    /** Kỳ liền trước để so sánh: lùi theo lịch với tháng, quý, năm; còn lại lùi đúng số ngày của kỳ. */
    public AnalyticsFilter previous() {
        LocalDate from = range.from();
        ReportPeriod previous = switch (period) {
            case "month" -> { LocalDate start = from.minusMonths(1); yield new ReportPeriod(start, start.with(TemporalAdjusters.lastDayOfMonth())); }
            case "quarter" -> { LocalDate start = from.minusMonths(3); yield new ReportPeriod(start, start.plusMonths(3).minusDays(1)); }
            case "year" -> { LocalDate start = from.minusYears(1); yield new ReportPeriod(start, start.plusYears(1).minusDays(1)); }
            default -> {
                long days = range.to().toEpochDay() - from.toEpochDay() + 1;
                yield new ReportPeriod(from.minusDays(days), from.minusDays(1));
            }
        };
        return new AnalyticsFilter(period, date, previous, groupBy, publisherId, categoryId, authorId);
    }

    public LocalDate bucket(LocalDate day) {
        return switch (groupBy) {
            case "week" -> day.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            case "month" -> day.withDayOfMonth(1);
            case "quarter" -> LocalDate.of(day.getYear(), ((day.getMonthValue() - 1) / 3) * 3 + 1, 1);
            case "year" -> day.withDayOfYear(1);
            default -> day;
        };
    }
}
