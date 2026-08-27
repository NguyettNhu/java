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

@Controller
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/admin/orders")
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) OrderStatus status,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("orders", orderService.search(new OrderSearchQuery(keyword, status), page, 20));
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", OrderStatus.values());
        return "admin/order-list";
    }

    @GetMapping("/admin/orders/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.adminOrder(id));
        return "admin/order-detail";
    }

    @PostMapping("/admin/orders/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam OrderStatus status,
                               RedirectAttributes redirectAttributes) {
        try {
            orderService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Trạng thái đơn hàng đã được cập nhật.");
        } catch (OrderException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }
}
