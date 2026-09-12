package com.yukihira.bookstore.admin;

import com.yukihira.bookstore.admin.report.ReportPeriod;
import com.yukihira.bookstore.admin.report.ReportService;
import com.yukihira.bookstore.book.BookSearchQuery;
import com.yukihira.bookstore.book.BookService;
import com.yukihira.bookstore.order.OrderSearchQuery;
import com.yukihira.bookstore.order.OrderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;

@Controller
public class AdminController {
    private final ReportService reportService;
    private final BookService bookService;
    private final OrderService orderService;

    public AdminController(ReportService reportService, BookService bookService, OrderService orderService) {
        this.reportService = reportService;
        this.bookService = bookService;
        this.orderService = orderService;
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("dashboard", reportService.dashboard());
        var books = new BookSearchQuery(null, null, null, null, null, "title");
        model.addAttribute("lowStock", bookService.searchAdmin(books, null, "low", 0, 5));
        model.addAttribute("outOfStock", bookService.searchAdmin(books, null, "empty", 0, 1).getTotalElements());
        model.addAttribute("recentOrders", orderService.search(new OrderSearchQuery(null, null), 0, 5));
        return "admin/dashboard";
    }

    @GetMapping("/admin/reports")
    public String reports(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                          Model model) {
        LocalDate today = LocalDate.now(ReportPeriod.ZONE);
        from = from == null ? today.withDayOfMonth(1) : from;
        to = to == null ? today : to;
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        try {
            model.addAttribute("report", reportService.report(new ReportPeriod(from, to)));
        } catch (IllegalArgumentException exception) {
            model.addAttribute("error", exception.getMessage());
        }
        return "admin/reports";
    }
}
