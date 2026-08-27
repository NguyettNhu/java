package com.yukihira.bookstore.order;

import com.yukihira.bookstore.cart.CartService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;

    public OrderController(OrderService orderService, CartService cartService) {
        this.orderService = orderService;
        this.cartService = cartService;
    }

    @GetMapping("/checkout")
    public String checkout(Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        var cart = cartService.getCart(authentication.getName());
        if (cart.items().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Hãy chọn ít nhất một cuốn sách trước khi đặt hàng.");
            return "redirect:/cart";
        }
        model.addAttribute("form", orderService.checkoutForm(authentication.getName()));
        model.addAttribute("cart", cart);
        model.addAttribute("paymentMethods", PaymentMethod.values());
        return "orders/checkout";
    }

    @PostMapping("/orders")
    public String placeOrder(Authentication authentication,
                             @Valid @ModelAttribute("form") CheckoutForm form,
                             BindingResult bindingResult, Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addCheckoutModel(authentication, model);
            return "orders/checkout";
        }
        try {
            Long orderId = orderService.checkout(authentication.getName(), form);
            redirectAttributes.addFlashAttribute("success", "Đơn hàng đã được tiếp nhận.");
            return "redirect:/orders/" + orderId;
        } catch (OrderException exception) {
            bindingResult.reject("checkout", exception.getMessage());
            addCheckoutModel(authentication, model);
            return "orders/checkout";
        }
    }

    @GetMapping("/orders")
    public String orders(Authentication authentication, Model model) {
        model.addAttribute("orders", orderService.customerOrders(authentication.getName()));
        return "orders/list";
    }

    @GetMapping("/orders/{id}")
    public String order(Authentication authentication, @PathVariable Long id, Model model,
                        RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("order", orderService.customerOrder(authentication.getName(), id));
            return "orders/detail";
        } catch (OrderException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/orders";
        }
    }

    @PostMapping("/orders/{id}/cancel")
    public String cancel(Authentication authentication, @PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        try {
            orderService.cancelByCustomer(authentication.getName(), id);
            redirectAttributes.addFlashAttribute("success", "Đơn hàng đã được hủy và tồn kho đã được hoàn lại.");
        } catch (OrderException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/orders/" + id;
    }

    private void addCheckoutModel(Authentication authentication, Model model) {
        model.addAttribute("cart", cartService.getCart(authentication.getName()));
        model.addAttribute("paymentMethods", PaymentMethod.values());
    }
}
