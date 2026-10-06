package com.yukihira.bookstore.admin.catalog;

import com.yukihira.bookstore.admin.Facet;
import com.yukihira.bookstore.admin.report.AnalyticsFilter;
import com.yukihira.bookstore.admin.report.AnalyticsParams;
import com.yukihira.bookstore.admin.report.AnalyticsService;
import com.yukihira.bookstore.admin.report.AnalyticsView.ReferenceStats;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AdminReferenceController {

    private final ReferenceDataService service;
    private final AnalyticsService analytics;

    public AdminReferenceController(ReferenceDataService service, AnalyticsService analytics) {
        this.service = service;
        this.analytics = analytics;
    }

    @GetMapping("/admin/{type:categories|authors|publishers}")
    public String list(@PathVariable String type, @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String details,
                       @RequestParam(defaultValue = "0") int minBooks,
                       @RequestParam(defaultValue = "all") String activity,
                       @RequestParam(defaultValue = "all") String availability,
                       @RequestParam(defaultValue = "all") String visibility,
                       @RequestParam(defaultValue = "name") String sort,
                       @RequestParam(defaultValue = "0") int page,
                       @ModelAttribute AnalyticsParams params, HttpServletRequest request, Model model) {
        ReferenceType referenceType = ReferenceType.fromPath(type);
        addTypeModel(model, referenceType);
        // Trang này chỉ cần kỳ thống kê cho cột “đã bán” và “doanh thu”; lọc theo danh mục để dành cho trang Báo cáo.
        AnalyticsFilter filter = params.periodOnly().apply(model);
        List<ReferenceStats> rows = filter == null ? List.of() : analytics.references(filter, referenceType);
        // Lối tắt “bán hàng trong kỳ” đếm trên các bộ lọc còn lại, để số trên mỗi nút khớp kết quả khi bấm.
        List<ReferenceStats> others = ReferenceAnalyticsSearch.filter(rows, keyword, details, minBooks, "all", availability, visibility, sort);
        long selling = others.stream().filter(r -> r.sold() > 0).count();
        model.addAttribute("activityFacets", List.of(
                Facet.of(request, "activity", null, "Tất cả", others.size()),
                Facet.of(request, "activity", "selling", "Có sách đã bán", selling),
                Facet.of(request, "activity", "unsold", "Chưa bán trong kỳ", others.size() - selling)));
        List<ReferenceStats> filtered = ReferenceAnalyticsSearch.filter(rows, keyword, details, minBooks, activity, availability, visibility, sort);
        model.addAttribute("summary", ReferenceSummary.of(filtered));
        model.addAttribute("charts", analytics.referenceCharts(referenceType, filtered));
        model.addAttribute("items", ReferenceAnalyticsSearch.page(filtered, page));
        model.addAttribute("keyword", keyword);
        model.addAttribute("details", details);
        model.addAttribute("minBooks", Math.max(0, minBooks));
        model.addAttribute("activity", activity);
        model.addAttribute("availability", availability);
        model.addAttribute("visibility", visibility);
        model.addAttribute("sort", sort);
        return "admin/reference-list";
    }

    @GetMapping("/admin/{type:categories|authors|publishers}/new")
    public String create(@PathVariable String type, Model model) {
        ReferenceType referenceType = ReferenceType.fromPath(type);
        addTypeModel(model, referenceType);
        model.addAttribute("form", new ReferenceForm());
        return "admin/reference-form";
    }

    @GetMapping("/admin/{type:categories|authors|publishers}/{id}/edit")
    public String edit(@PathVariable String type, @PathVariable Long id, Model model) {
        ReferenceType referenceType = ReferenceType.fromPath(type);
        addTypeModel(model, referenceType);
        model.addAttribute("form", service.getForm(referenceType, id));
        return "admin/reference-form";
    }

    @PostMapping("/admin/{type:categories|authors|publishers}/save")
    public String save(@PathVariable String type,
                       @Valid @ModelAttribute("form") ReferenceForm form,
                       BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
                // Lưu danh mục tham chiếu và hiển thị lỗi ngay trên form khi cần.
        ReferenceType referenceType = ReferenceType.fromPath(type);
        if (!bindingResult.hasErrors()) {
            try {
                service.save(referenceType, form);
                redirectAttributes.addFlashAttribute("success", referenceType.getLabel() + " đã được lưu.");
                return "redirect:/admin/" + type;
            } catch (CatalogValidationException exception) {
                bindingResult.rejectValue(exception.getField(), "catalog", exception.getMessage());
            } catch (DataIntegrityViolationException exception) {
                bindingResult.rejectValue("name", "duplicate", "Tên hoặc đường dẫn đã tồn tại. Hãy kiểm tra lại.");
            }
        }
        addTypeModel(model, referenceType);
        return "admin/reference-form";
    }

    @PostMapping("/admin/{type:categories|authors|publishers}/{id}/delete")
    public String delete(@PathVariable String type, @PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        ReferenceType referenceType = ReferenceType.fromPath(type);
        try {
            redirectAttributes.addFlashAttribute("success", service.delete(referenceType, id));
        } catch (IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        } catch (DataIntegrityViolationException exception) {
            redirectAttributes.addFlashAttribute("error", "Mục này vừa được liên kết với sách, chưa thể xóa.");
        }
        return "redirect:/admin/" + type;
    }

    private void addTypeModel(Model model, ReferenceType type) {
        model.addAttribute("type", type);
        model.addAttribute("basePath", "/admin/" + type.getPath());
    }
}
