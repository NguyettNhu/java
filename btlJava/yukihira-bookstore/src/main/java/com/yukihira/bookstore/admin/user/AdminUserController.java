package com.yukihira.bookstore.admin.user;

import com.yukihira.bookstore.user.UserSearchQuery;
import com.yukihira.bookstore.user.UserService;
import com.yukihira.bookstore.user.UserStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminUserController {

    private final UserService userService;
    private final com.yukihira.bookstore.order.OrderService orderService;

    public AdminUserController(UserService userService, com.yukihira.bookstore.order.OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/admin/users/{id}")
    public String detail(@PathVariable Long id, Model model) {
        var customer = userService.customer(id);
        model.addAttribute("customer", customer);
        model.addAttribute("orders", orderService.customerOrders(customer.email()));
        return "admin/user-detail";
    }

    @GetMapping("/admin/users")
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) UserStatus status,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("users", userService.searchCustomers(new UserSearchQuery(keyword, status), page, 20));
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", UserStatus.values());
        return "admin/user-list";
    }

    @PostMapping("/admin/users/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam UserStatus status,
                               RedirectAttributes redirectAttributes) {
        try {
            userService.updateCustomerStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Trạng thái tài khoản đã được cập nhật.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/admin/users";
    }
}
