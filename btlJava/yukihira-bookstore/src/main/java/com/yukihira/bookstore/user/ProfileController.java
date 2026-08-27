package com.yukihira.bookstore.user;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        model.addAttribute("form", userService.profile(authentication.getName()));
        model.addAttribute("email", authentication.getName());
        return "user/profile";
    }

    @PostMapping("/profile")
    public String update(Authentication authentication,
                         @Valid @ModelAttribute("form") ProfileForm form,
                         BindingResult bindingResult, Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("email", authentication.getName());
            return "user/profile";
        }
        userService.updateProfile(authentication.getName(), form);
        redirectAttributes.addFlashAttribute("success", "Thông tin cá nhân đã được cập nhật.");
        return "redirect:/profile";
    }
}
