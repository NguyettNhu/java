package com.yukihira.bookstore.admin;

import com.yukihira.bookstore.admin.report.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final ReportService reportService;

    public AdminController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("dashboard", reportService.dashboard());
        return "admin/dashboard";
    }

    @GetMapping("/admin/reports")
    public String reports(Model model) {
        model.addAttribute("dashboard", reportService.dashboard());
        return "admin/reports";
    }
}
