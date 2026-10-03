package com.yukihira.bookstore.admin.report;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;

/**
 * Trả về riêng khối số liệu phân tích dưới dạng mảnh HTML. Trước đây phần này được tính
 * đồng bộ cho mọi trang admin nên mỗi lần chuyển trang đều phải chờ quét toàn bộ catalog
 * và đơn hàng; giờ trình duyệt tải nó sau khi trang đã hiển thị.
 */
@Controller
public class AdminAnalyticsController {
    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
            // Trang báo cáo có bố cục chi tiết riêng; các trang quản lý dùng khối tóm tắt chung.
            return "fragments/admin-analytics :: " + (scope.section().equals("reports") ? "report" : "panel");
        } catch (DateTimeParseException | NumberFormatException exception) {
            model.addAttribute("analyticsError", "Ngày hoặc mã danh mục không hợp lệ. Hãy kiểm tra bộ lọc.");
            return "fragments/admin-analytics :: panelError";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("analyticsError", exception.getMessage());
            return "fragments/admin-analytics :: panelError";
        }
    }

    /** Xuất toàn bộ số liệu của trang báo cáo theo đúng bộ lọc đang xem thành tệp CSV mở được bằng Excel. */
    @GetMapping("/admin/reports/export")
    public ResponseEntity<byte[]> export(@RequestParam(required = false) String period,
                                         @RequestParam(required = false) String date,
                                         @RequestParam(required = false) String from,
                                         @RequestParam(required = false) String to,
                                         @RequestParam(required = false) String groupBy,
                                         @RequestParam(required = false) String reportPublisher,
                                         @RequestParam(required = false) String reportCategory,
                                         @RequestParam(required = false) String reportAuthor) {
        AnalyticsFilter filter;
        try {
            filter = AnalyticsFilter.resolve(period, date(date), date(from), date(to), groupBy,
                    id(reportPublisher), id(reportCategory), id(reportAuthor));
        } catch (DateTimeParseException | NumberFormatException exception) {
            return ResponseEntity.badRequest().contentType(new MediaType("text", "plain", StandardCharsets.UTF_8))
                    .body("Ngày hoặc mã danh mục không hợp lệ.".getBytes(StandardCharsets.UTF_8));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().contentType(new MediaType("text", "plain", StandardCharsets.UTF_8))
                    .body(exception.getMessage().getBytes(StandardCharsets.UTF_8));
        }
        AnalyticsView view = analytics.build(filter, "reports", null);
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

    private LocalDate date(String value) {
        return value == null || value.isBlank() ? null : LocalDate.parse(value);
    }
    private Long id(String value) {
        return value == null || value.isBlank() ? null : Long.valueOf(value);
    }
}
