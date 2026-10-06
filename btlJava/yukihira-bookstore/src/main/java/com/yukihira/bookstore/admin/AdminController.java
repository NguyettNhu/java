package com.yukihira.bookstore.admin;

import com.yukihira.bookstore.admin.report.ReportService;
import com.yukihira.bookstore.book.BookSearchQuery;
import com.yukihira.bookstore.book.BookService;
import com.yukihira.bookstore.order.OrderSearchQuery;
import com.yukihira.bookstore.order.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
        // Gom số liệu tổng quan, tồn kho và đơn hàng mới cho dashboard.
        model.addAttribute("dashboard", reportService.dashboard());
        var books = new BookSearchQuery(null, null, null, null, null, "title");
        model.addAttribute("lowStock", bookService.searchAdmin(books, null, "low", 0, 5));
        model.addAttribute("outOfStock", bookService.searchAdmin(books, null, "empty", 0, 1).getTotalElements());
        model.addAttribute("recentOrders", orderService.search(new OrderSearchQuery(null, null), 0, 5));
        return "admin/dashboard";
    }
}
