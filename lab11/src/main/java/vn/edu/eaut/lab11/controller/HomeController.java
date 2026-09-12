package vn.edu.eaut.lab11.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Year;
import java.util.List;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Trang chủ Lab 11");
        model.addAttribute("greeting", "Ứng dụng Spring Boot đã sẵn sàng!");
        model.addAttribute("message",
                "Nội dung này được HomeController gửi sang giao diện thông qua đối tượng Model.");
        model.addAttribute("studentName", "Sinh viên Java Web");
        model.addAttribute("technologies", List.of("Spring Web", "Thymeleaf", "DevTools"));
        model.addAttribute("currentYear", Year.now().getValue());

        return "home";
    }
}
