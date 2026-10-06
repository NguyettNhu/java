package com.yukihira.bookstore.admin.user;

import com.yukihira.bookstore.admin.Facet;
import com.yukihira.bookstore.admin.report.AdminSummaryService;
import com.yukihira.bookstore.admin.report.AdminSummaryService.CustomerSummary;
import com.yukihira.bookstore.user.UserSearchQuery;
import com.yukihira.bookstore.user.UserService;
import com.yukihira.bookstore.user.UserStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AdminUserController {

    private final UserService userService;
    private final com.yukihira.bookstore.order.OrderService orderService;
    private final AdminSummaryService summaries;

    public AdminUserController(UserService userService, com.yukihira.bookstore.order.OrderService orderService,
                               AdminSummaryService summaries) {
        this.userService = userService;
        this.orderService = orderService;
        this.summaries = summaries;
    }

    @GetMapping("/admin/users/{id}")
    public String detail(@PathVariable Long id, Model model) {
        var customer = userService.customer(id);
        model.addAttribute("customer", customer);
        var orders = orderService.customerOrders(customer.email());
        model.addAttribute("orders", orders);
        // Lịch sử mua đã nằm sẵn trong danh sách đơn của khách nên tóm tắt không cần truy vấn thêm.
        model.addAttribute("summary", CustomerSummary.of(orders));
        return "admin/user-detail";
    }

    @GetMapping("/admin/users")
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) UserStatus status,
                       @RequestParam(defaultValue = "0") int page, HttpServletRequest request, Model model) {
                // Tải danh sách khách hàng theo từ khóa, trạng thái và trang hiện tại.
        model.addAttribute("users", userService.searchCustomers(new UserSearchQuery(keyword, status), page, 20));
        var summary = summaries.customers(keyword, status);
        model.addAttribute("summary", summary);
        model.addAttribute("statusFacets", List.of(
                Facet.of(request, "status", null, "Mọi trạng thái", summary.statusTotal()),
                Facet.of(request, "status", UserStatus.ACTIVE.name(), UserStatus.ACTIVE.getLabel(), summary.count(UserStatus.ACTIVE)),
                Facet.of(request, "status", UserStatus.LOCKED.name(), UserStatus.LOCKED.getLabel(), summary.count(UserStatus.LOCKED))));
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", UserStatus.values());
        return "admin/user-list";
    }

    @PostMapping("/admin/users/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam UserStatus status,
                               RedirectAttributes redirectAttributes) {
        // Cập nhật trạng thái tài khoản và phản hồi kết quả qua redirect.
        try {
            userService.updateCustomerStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Trạng thái tài khoản đã được cập nhật.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/admin/users";
    }
}
