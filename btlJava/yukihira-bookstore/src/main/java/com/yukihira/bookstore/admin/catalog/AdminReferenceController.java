package com.yukihira.bookstore.admin.catalog;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminReferenceController {

    private final ReferenceDataService service;

    public AdminReferenceController(ReferenceDataService service) {
        this.service = service;
    }

    @GetMapping("/admin/{type:categories|authors|publishers}")
    public String list(@PathVariable String type, Model model) {
        ReferenceType referenceType = ReferenceType.fromPath(type);
        addTypeModel(model, referenceType);
        model.addAttribute("items", service.list(referenceType));
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
        ReferenceType referenceType = ReferenceType.fromPath(type);
        if (!bindingResult.hasErrors()) {
            try {
                service.save(referenceType, form);
                redirectAttributes.addFlashAttribute("success", referenceType.getLabel() + " đã được lưu.");
                return "redirect:/admin/" + type;
            } catch (IllegalArgumentException exception) {
                bindingResult.rejectValue("name", "duplicate", exception.getMessage());
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
            service.delete(referenceType, id);
            redirectAttributes.addFlashAttribute("success", referenceType.getLabel() + " đã được cập nhật.");
        } catch (IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/admin/" + type;
    }

    private void addTypeModel(Model model, ReferenceType type) {
        model.addAttribute("type", type);
        model.addAttribute("basePath", "/admin/" + type.getPath());
    }
}
