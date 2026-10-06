package com.yukihira.bookstore.admin.report;

import com.yukihira.bookstore.admin.catalog.ReferenceDataService;
import com.yukihira.bookstore.admin.catalog.ReferenceType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

/**
 * Trang Báo cáo là nơi duy nhất có phân tích đầy đủ theo kỳ. Khối số liệu được trả về riêng
 * dưới dạng mảnh HTML và tải sau khi trang đã hiển thị, nên mở trang không phải chờ tổng hợp.
 */
@Controller
public class AdminAnalyticsController {
    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final AnalyticsService analytics;
    private final ReferenceDataService references;

    public AdminAnalyticsController(AnalyticsService analytics, ReferenceDataService references) {
        this.analytics = analytics;
        this.references = references;
    }

    @GetMapping("/admin/reports")
    public String reports(@ModelAttribute AnalyticsParams params, HttpServletRequest request, Model model) {
        // Nạp các lựa chọn tham chiếu cho bộ lọc nhà xuất bản, thể loại và tác giả (đã được cache).
        model.addAttribute("reportPublishers", references.list(ReferenceType.PUBLISHERS));
        model.addAttribute("reportCategories", references.list(ReferenceType.CATEGORIES));
        model.addAttribute("reportAuthors", references.list(ReferenceType.AUTHORS));
        // Kiểm tra bộ lọc ngay tại đây để báo lỗi trước khi trình duyệt bỏ công gọi endpoint số liệu.
        if (params.apply(model) != null) {
            String query = request.getQueryString();
            model.addAttribute("analyticsUrl", request.getContextPath() + "/admin/reports/panel"
                    + (query == null || query.isBlank() ? "" : "?" + query));
        }
        return "admin/reports";
    }

    @GetMapping("/admin/reports/panel")
    public String panel(@ModelAttribute AnalyticsParams params, Model model) {
        try {
            model.addAttribute("analytics", analytics.build(params.resolve()));
            return "fragments/admin-analytics :: report";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("analyticsError", exception.getMessage());
            return "fragments/admin-analytics :: panelError";
        }
    }

    /** Xuất toàn bộ số liệu của trang báo cáo theo đúng bộ lọc đang xem thành tệp CSV mở được bằng Excel. */
    @GetMapping("/admin/reports/export")
    public ResponseEntity<byte[]> export(@ModelAttribute AnalyticsParams params) {
        AnalyticsFilter filter;
        try {
            filter = params.resolve();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().contentType(new MediaType("text", "plain", StandardCharsets.UTF_8))
                    .body(exception.getMessage().getBytes(StandardCharsets.UTF_8));
        }
        AnalyticsView view = analytics.build(filter);
        String name = "bao-cao-" + filter.range().from() + "_" + filter.range().to() + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(name).build().toString())
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(csv(view).getBytes(StandardCharsets.UTF_8));
    }

    private String csv(AnalyticsView a) {
        // Ký tự BOM giúp Excel nhận đúng tiếng Việt; số để dạng thô để có thể tính toán tiếp.
        StringBuilder out = new StringBuilder("﻿");
        var p = a.previous();
        row(out, "Báo cáo cửa hàng Yukihira");
        row(out, "Kỳ thống kê", DAY.format(a.filter().range().from()) + " - " + DAY.format(a.filter().range().to()));
        row(out, "Kỳ so sánh", DAY.format(p.range().from()) + " - " + DAY.format(p.range().to()));
        row(out, "Ghi chú", "Theo ngày đặt hàng, giờ Việt Nam. Doanh thu chỉ tính đơn hoàn thành.");
        out.append("\r\n");
        row(out, "Chỉ số", "Kỳ này", "Kỳ trước", "Thay đổi (%)");
        row(out, "Doanh thu (₫)", num(a.revenue()), num(p.revenue()), num(a.revenueChange()));
        row(out, "Tổng đơn (mọi trạng thái)", num(a.totalOrders()), num(p.totalOrders()), num(a.ordersChange()));
        row(out, "Đơn hoàn thành", num(a.completedOrders()), num(p.completedOrders()), "");
        row(out, "Số cuốn đã bán", num(a.quantitySold()), num(p.quantitySold()), num(a.quantityChange()));
        row(out, "Khách đã mua", num(a.buyers()), num(p.buyers()), num(a.buyersChange()));
        row(out, "Giá trị trung bình / đơn hoàn thành (₫)", num(a.averageOrderValue()), num(p.averageOrderValue()), num(a.averageOrderValueChange()));
        out.append("\r\n");
        row(out, "Trạng thái đơn", "Số đơn", "Tỉ lệ (%)", "Giá trị (₫)");
        for (OrderStatusTotal s : a.statusTotals())
            row(out, s.status().getLabel(), num(s.count()), num(a.statusShare(s)), num(s.amount()));
        out.append("\r\n");
        row(out, "Tồn kho hiện tại");
        row(out, "Tổng số đầu sách", num(a.totalBooks()));
        row(out, "Tổng tồn kho (cuốn)", num(a.stock()));
        row(out, "Đầu sách sắp hết (1-5 cuốn)", num(a.lowStock()));
        row(out, "Đầu sách hết hàng", num(a.emptyStock()));
        row(out, "Giá trị tồn theo giá bán (₫)", num(a.stockValue()));
        for (AnalyticsView.Chart chart : a.charts()) {
            out.append("\r\n");
            row(out, chart.title());
            row(out, "Mục", chart.unit());
            for (AnalyticsView.Point point : chart.points()) row(out, point.label(), num(point.value()));
        }
        return out.toString();
    }

    private void row(StringBuilder out, String... cells) {
        for (int i = 0; i < cells.length; i++) {
            if (i > 0) out.append(',');
            String cell = cells[i] == null ? "" : cells[i];
            // Chặn công thức bảng tính do tên sách hoặc tên khách bắt đầu bằng ký tự đặc biệt.
            if (!cell.isEmpty() && "=+-@".indexOf(cell.charAt(0)) >= 0 && !cell.matches("-?\\d+(\\.\\d+)?")) cell = "'" + cell;
            out.append('"').append(cell.replace("\"", "\"\"")).append('"');
        }
        out.append("\r\n");
    }

    private String num(BigDecimal value) {
        return value == null ? "" : value.stripTrailingZeros().toPlainString();
    }
    private String num(long value) {
        return Long.toString(value);
    }
}
