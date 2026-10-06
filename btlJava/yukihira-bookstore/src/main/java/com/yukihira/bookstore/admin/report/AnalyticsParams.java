package com.yukihira.bookstore.admin.report;

import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Tham số kỳ thống kê nhận nguyên dạng chuỗi từ form, để ngày hoặc mã sai được báo lỗi
 * trên trang thay vì làm hỏng cả yêu cầu.
 */
public record AnalyticsParams(String period, String date, String from, String to, String groupBy,
                              String reportPublisher, String reportCategory, String reportAuthor) {
    static final String INVALID = "Ngày hoặc mã danh mục không hợp lệ. Hãy kiểm tra bộ lọc.";

    /** Chuẩn hóa kỳ thống kê; mọi lỗi định dạng đều trả về cùng một thông báo dễ hiểu. */
    public AnalyticsFilter resolve() {
        try {
            return AnalyticsFilter.resolve(period, date(date), date(from), date(to), groupBy,
                    id(reportPublisher), id(reportCategory), id(reportAuthor));
        } catch (DateTimeParseException | NumberFormatException exception) {
            throw new IllegalArgumentException(INVALID, exception);
        }
    }

    /** Trang thể loại, tác giả và nhà xuất bản chỉ dùng kỳ thống kê, không lọc theo danh mục. */
    public AnalyticsParams periodOnly() {
        return new AnalyticsParams(period, date, from, to, null, null, null, null);
    }

    /**
     * Đưa giá trị lọc vào model để form hiển thị đúng trạng thái. Trả về null và ghi
     * {@code analyticsError} khi bộ lọc không hợp lệ.
     */
    public AnalyticsFilter apply(Model model) {
        // Giữ nguyên giá trị người dùng nhập để họ sửa lại được khi bộ lọc bị từ chối.
        model.addAttribute("aperiod", period); model.addAttribute("adate", date);
        model.addAttribute("afrom", from); model.addAttribute("ato", to); model.addAttribute("agroupBy", groupBy);
        model.addAttribute("areportPublisher", reportPublisher);
        model.addAttribute("areportCategory", reportCategory);
        model.addAttribute("areportAuthor", reportAuthor);
        try {
            AnalyticsFilter filter = resolve();
            model.addAttribute("aperiod", filter.period()); model.addAttribute("adate", filter.date());
            model.addAttribute("afrom", filter.range().from()); model.addAttribute("ato", filter.range().to());
            model.addAttribute("agroupBy", filter.groupBy());
            return filter;
        } catch (IllegalArgumentException exception) {
            model.addAttribute("analyticsError", exception.getMessage());
            return null;
        }
    }

    private static LocalDate date(String value) {
        return value == null || value.isBlank() ? null : LocalDate.parse(value.trim());
    }

    private static Long id(String value) {
        return value == null || value.isBlank() ? null : Long.valueOf(value.trim());
    }
}
