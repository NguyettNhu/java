package com.yukihira.bookstore.admin.order;

import com.yukihira.bookstore.order.OrderException;
import com.yukihira.bookstore.order.OrderSearchQuery;
import com.yukihira.bookstore.order.OrderService;
import com.yukihira.bookstore.order.OrderStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import org.springframework.data.domain.Page;

@Controller
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/admin/orders")
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) OrderStatus status,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                       @RequestParam(defaultValue = "0") int page, Model model) {
                // Tìm kiếm đơn hàng theo bộ lọc và trả về trang rỗng nếu ngày không hợp lệ.
        try {
            model.addAttribute("orders", orderService.search(new OrderSearchQuery(keyword, status, from, to), page, 20));
        } catch (IllegalArgumentException exception) {
            model.addAttribute("orders", Page.empty());
            model.addAttribute("error", exception.getMessage());
        }
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", OrderStatus.values());
        return "admin/order-list";
    }

    @GetMapping("/admin/orders/{id}")
    public String detail(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("order", orderService.adminOrder(id));
        } catch (OrderException exception) {
            throw new java.util.NoSuchElementException();
        }
        return "admin/order-detail";
    }

    @PostMapping("/admin/orders/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam OrderStatus status,
                               RedirectAttributes redirectAttributes) {
        // Chuyển trạng thái đơn qua service để áp dụng policy nghiệp vụ.
        try {
            orderService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Trạng thái đơn hàng đã được cập nhật.");
        } catch (OrderException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }
}
