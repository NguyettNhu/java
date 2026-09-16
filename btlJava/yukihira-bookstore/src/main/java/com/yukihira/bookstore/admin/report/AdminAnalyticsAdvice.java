package com.yukihira.bookstore.admin.report;

import com.yukihira.bookstore.admin.catalog.ReferenceDataService;
import com.yukihira.bookstore.admin.catalog.ReferenceType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@ControllerAdvice(basePackages = "com.yukihira.bookstore.admin", annotations = Controller.class)
public class AdminAnalyticsAdvice {
    private final AnalyticsService analytics;
    private final ReferenceDataService references;
    public AdminAnalyticsAdvice(AnalyticsService analytics, ReferenceDataService references) {
        this.analytics = analytics; this.references = references;
    }

    @ModelAttribute
    public void analytics(HttpServletRequest request, Model model) {
        // Chỉ chuẩn bị bộ lọc dùng chung cho các trang GET của admin. Việc tổng hợp số liệu
        // được tách sang AdminAnalyticsController và tải sau khi trang đã hiện, nên chuyển
        // trang không còn phải chờ quét toàn bộ catalog và đơn hàng.
        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (!request.getMethod().equals("GET")) return;
        AnalyticsScope scope = AnalyticsScope.of(path);
        if (scope == null) return;
        // Xác định trang hiện tại để form lọc và khối số liệu biết đang đứng ở đâu.
        model.addAttribute("analyticsPath", path);
        // Nạp các lựa chọn tham chiếu cho bộ lọc nhà xuất bản, thể loại và tác giả (đã được cache).
        model.addAttribute("reportPublishers", references.list(ReferenceType.PUBLISHERS));
        model.addAttribute("reportCategories", references.list(ReferenceType.CATEGORIES));
        model.addAttribute("reportAuthors", references.list(ReferenceType.AUTHORS));
        // Giữ lại toàn bộ tham số lọc để form và phân trang hiển thị đúng trạng thái hiện tại.
        for (String key : new String[]{"period", "date", "from", "to", "groupBy", "reportPublisher", "reportCategory", "reportAuthor"})
            model.addAttribute("a" + key, request.getParameter(key));
        try {
            // Chuẩn hóa kỳ thống kê, khoảng ngày và các mã tham chiếu ngay tại đây để phát hiện
            // tham số sai trước khi trình duyệt bỏ công gọi endpoint số liệu.
            var filter = AnalyticsFilter.resolve(request.getParameter("period"), date(request, "date"), date(request, "from"), date(request, "to"),
                    request.getParameter("groupBy"), id(request, "reportPublisher"), id(request, "reportCategory"), id(request, "reportAuthor"));
            model.addAttribute("aperiod", filter.period()); model.addAttribute("adate", filter.date());
            model.addAttribute("afrom", filter.range().from()); model.addAttribute("ato", filter.range().to());
            model.addAttribute("agroupBy", filter.groupBy());
            if (scope.inlineAnalytics()) {
                // Trang thể loại, tác giả và nhà xuất bản dựng bảng quản lý từ chính số liệu này.
                model.addAttribute("analyticsDetail", scope.detail());
                model.addAttribute("analyticsCatalog", scope.catalog());
                model.addAttribute("analytics", analytics.build(filter, scope.section(), scope.detailId()));
            } else {
                model.addAttribute("analyticsUrl", panelUrl(request, path));
            }
        } catch (DateTimeParseException | NumberFormatException exception) {
            // Hiển thị lỗi thống nhất khi ngày hoặc mã bộ lọc không thể chuyển đổi.
            model.addAttribute("analyticsError", "Ngày hoặc mã danh mục không hợp lệ. Hãy kiểm tra bộ lọc.");
            model.addAttribute("error", "Ngày hoặc mã danh mục không hợp lệ. Hãy kiểm tra bộ lọc.");
        } catch (IllegalArgumentException exception) {
            // Hiển thị thông báo nghiệp vụ như khoảng ngày hoặc cách nhóm không hợp lệ.
            model.addAttribute("analyticsError", exception.getMessage());
            model.addAttribute("error", exception.getMessage());
        }
    }

    private String panelUrl(HttpServletRequest request, String path) {
        // Gửi kèm nguyên chuỗi truy vấn để khối số liệu dùng đúng bộ lọc đang hiển thị trên form.
        String query = request.getQueryString();
        return request.getContextPath() + "/admin/analytics?for=" + URLEncoder.encode(path, StandardCharsets.UTF_8)
                + (query == null || query.isBlank() ? "" : "&" + query);
    }

    private LocalDate date(HttpServletRequest request, String name) {
        // Chuyển tham số ngày rỗng thành null để bộ lọc tự chọn giá trị mặc định.
        String value = request.getParameter(name);
        return value == null || value.isBlank() ? null : LocalDate.parse(value);
    }
    private Long id(HttpServletRequest request, String name) {
        // Chuyển mã tham chiếu từ request sang số hoặc trả về null nếu chưa chọn.
        String value = request.getParameter(name);
        return value == null || value.isBlank() ? null : Long.valueOf(value);
    }
}
