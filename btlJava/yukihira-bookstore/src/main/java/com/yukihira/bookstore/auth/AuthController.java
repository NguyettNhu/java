package com.yukihira.bookstore.auth;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new RegistrationForm());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegistrationForm form,
                           BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch", "Mật khẩu xác nhận không khớp");
        }
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        try {
            authService.register(form);
        } catch (DuplicateEmailException exception) {
            bindingResult.rejectValue("email", "email.duplicate", exception.getMessage());
            return "auth/register";
        }
        redirectAttributes.addFlashAttribute("success", "Tài khoản đã được tạo. Bạn có thể đăng nhập ngay.");
        return "redirect:/login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/403";
    }
}
