package com.yukihira.bookstore.admin.report;

import com.yukihira.bookstore.admin.catalog.ReferenceDataService;
import com.yukihira.bookstore.admin.catalog.ReferenceType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Set;

@ControllerAdvice(basePackages = "com.yukihira.bookstore.admin", annotations = Controller.class)
public class AdminAnalyticsAdvice {
    private final AnalyticsService analytics;
    private final ReferenceDataService references;
    public AdminAnalyticsAdvice(AnalyticsService analytics, ReferenceDataService references) {
        this.analytics = analytics; this.references = references;
    }

    @ModelAttribute
    public void analytics(HttpServletRequest request, Model model) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (!request.getMethod().equals("GET") || !path.matches("/admin(?:/(books|users|orders)(/\\d+)?|/(reports|inventory|categories|authors|publishers))?")) return;
        String[] parts = path.split("/");
        String section = parts.length > 2 ? parts[2] : "dashboard";
        Long detail = parts.length > 3 ? Long.valueOf(parts[3]) : null;
        model.addAttribute("analyticsPath", path);
        model.addAttribute("analyticsDetail", detail != null);
        model.addAttribute("analyticsCatalog", Set.of("dashboard", "reports", "books", "inventory", "categories", "publishers", "authors").contains(section));
        model.addAttribute("reportPublishers", references.list(ReferenceType.PUBLISHERS));
        model.addAttribute("reportCategories", references.list(ReferenceType.CATEGORIES));
        model.addAttribute("reportAuthors", references.list(ReferenceType.AUTHORS));
        for (String key : new String[]{"period", "date", "from", "to", "groupBy", "reportPublisher", "reportCategory", "reportAuthor"})
            model.addAttribute("a" + key, request.getParameter(key));
        try {
            var filter = AnalyticsFilter.resolve(request.getParameter("period"), date(request, "date"), date(request, "from"), date(request, "to"),
                    request.getParameter("groupBy"), id(request, "reportPublisher"), id(request, "reportCategory"), id(request, "reportAuthor"));
            model.addAttribute("aperiod", filter.period()); model.addAttribute("adate", filter.date());
            model.addAttribute("afrom", filter.range().from()); model.addAttribute("ato", filter.range().to());
            model.addAttribute("agroupBy", filter.groupBy());
            model.addAttribute("analytics", analytics.build(filter, section, detail));
        } catch (DateTimeParseException | NumberFormatException exception) {
            model.addAttribute("analyticsError", "Ngày hoặc mã danh mục không hợp lệ. Hãy kiểm tra bộ lọc.");
            model.addAttribute("error", "Ngày hoặc mã danh mục không hợp lệ. Hãy kiểm tra bộ lọc.");
        } catch (IllegalArgumentException exception) {
            model.addAttribute("analyticsError", exception.getMessage());
            model.addAttribute("error", exception.getMessage());
        }
    }
    private LocalDate date(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null || value.isBlank() ? null : LocalDate.parse(value);
    }
    private Long id(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null || value.isBlank() ? null : Long.valueOf(value);
    }
}
