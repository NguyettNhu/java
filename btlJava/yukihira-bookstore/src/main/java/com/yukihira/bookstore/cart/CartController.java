package com.yukihira.bookstore.cart;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/cart")
    public String cart(Authentication authentication, Model model) {
        model.addAttribute("cart", cartService.getCart(authentication.getName()));
        return "cart/view";
    }

    @PostMapping("/cart/items")
    public String add(Authentication authentication, @RequestParam Long bookId,
                      @RequestParam(defaultValue = "1") int quantity,
                      RedirectAttributes redirectAttributes) {
        try {
            cartService.add(authentication.getName(), bookId, quantity);
            redirectAttributes.addFlashAttribute("success", "Đã thêm sách vào giỏ.");
        } catch (CartException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/items/{id}/update")
    public String update(Authentication authentication, @PathVariable Long id,
                         @RequestParam int quantity, RedirectAttributes redirectAttributes) {
        try {
            cartService.update(authentication.getName(), id, quantity);
            redirectAttributes.addFlashAttribute("success", "Số lượng đã được cập nhật.");
        } catch (CartException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/items/{id}/delete")
    public String remove(Authentication authentication, @PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        try {
            cartService.remove(authentication.getName(), id);
            redirectAttributes.addFlashAttribute("success", "Đã bỏ sách khỏi giỏ.");
        } catch (CartException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/cart";
    }
}
