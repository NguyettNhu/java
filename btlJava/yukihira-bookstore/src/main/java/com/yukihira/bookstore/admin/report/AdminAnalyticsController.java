package com.yukihira.bookstore.admin.report;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;

/**
 * Trả về riêng khối số liệu phân tích dưới dạng mảnh HTML. Trước đây phần này được tính
 * đồng bộ cho mọi trang admin nên mỗi lần chuyển trang đều phải chờ quét toàn bộ catalog
 * và đơn hàng; giờ trình duyệt tải nó sau khi trang đã hiển thị.
 */
@Controller
public class AdminAnalyticsController {

    private final AnalyticsService analytics;

    public AdminAnalyticsController(AnalyticsService analytics) {
        this.analytics = analytics;
    }

    @GetMapping("/admin/analytics")
    public String panel(@RequestParam(name = "for", required = false) String target,
                        @RequestParam(required = false) String period,
                        @RequestParam(required = false) String date,
                        @RequestParam(required = false) String from,
                        @RequestParam(required = false) String to,
                        @RequestParam(required = false) String groupBy,
                        @RequestParam(required = false) String reportPublisher,
                        @RequestParam(required = false) String reportCategory,
                        @RequestParam(required = false) String reportAuthor,
                        Model model) {
        // Chỉ chấp nhận đường dẫn admin thật sự có báo cáo, vì tham số này do client gửi lên.
        AnalyticsScope scope = AnalyticsScope.of(target);
        if (scope == null) throw new NoSuchElementException();
        // Lặp lại các tham số lọc để phần chú thích trong mảnh HTML mô tả đúng phạm vi đang xem.
        model.addAttribute("analyticsDetail", scope.detail());
        model.addAttribute("analyticsCatalog", scope.catalog());
        model.addAttribute("areportPublisher", reportPublisher);
        model.addAttribute("areportCategory", reportCategory);
        model.addAttribute("areportAuthor", reportAuthor);
        try {
            var filter = AnalyticsFilter.resolve(period, date(date), date(from), date(to), groupBy,
                    id(reportPublisher), id(reportCategory), id(reportAuthor));
            model.addAttribute("analytics", analytics.build(filter, scope.section(), scope.detailId()));
            return "fragments/admin-analytics :: panel";
        } catch (DateTimeParseException | NumberFormatException exception) {
            model.addAttribute("analyticsError", "Ngày hoặc mã danh mục không hợp lệ. Hãy kiểm tra bộ lọc.");
            return "fragments/admin-analytics :: panelError";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("analyticsError", exception.getMessage());
            return "fragments/admin-analytics :: panelError";
        }
    }

    private LocalDate date(String value) {
        return value == null || value.isBlank() ? null : LocalDate.parse(value);
    }
    private Long id(String value) {
        return value == null || value.isBlank() ? null : Long.valueOf(value);
    }
}
