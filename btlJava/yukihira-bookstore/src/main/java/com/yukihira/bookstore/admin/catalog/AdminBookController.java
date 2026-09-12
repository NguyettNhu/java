package com.yukihira.bookstore.admin.catalog;

import com.yukihira.bookstore.book.BookForm;
import com.yukihira.bookstore.book.BookSearchQuery;
import com.yukihira.bookstore.book.BookService;
import com.yukihira.bookstore.book.BookStatus;
import com.yukihira.bookstore.book.StockForm;
import org.springframework.dao.DataIntegrityViolationException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminBookController {

    private final BookService bookService;

    public AdminBookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/admin/books")
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) BookStatus status,
                       @RequestParam(defaultValue = "") String stock,
                       @RequestParam(defaultValue = "newest") String sort,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("books", bookService.searchAdmin(
                new BookSearchQuery(keyword, categoryId, null, null, null, sort), status, stock, page, 20));
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("stockFilter", stock);
        model.addAttribute("sort", sort);
        addReferences(model);
        return "admin/book-list";
    }

    @GetMapping("/admin/books/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("book", bookService.adminBook(id));
        return "admin/book-detail";
    }

    @GetMapping("/admin/inventory")
    public String inventory(@RequestParam(required = false) String keyword,
                            @RequestParam(defaultValue = "") String stock,
                            @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("books", bookService.searchAdmin(
                new BookSearchQuery(keyword, null, null, null, null, "title"), null, stock, page, 20));
        model.addAttribute("keyword", keyword);
        model.addAttribute("stockFilter", stock);
        return "admin/inventory";
    }

    @PostMapping("/admin/inventory/{id}")
    public String updateStock(@PathVariable Long id, @Valid @ModelAttribute StockForm form,
                              BindingResult errors, RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            redirect.addFlashAttribute("error", errors.getAllErrors().getFirst().getDefaultMessage());
        } else {
            try {
                bookService.updateStock(id, form);
                redirect.addFlashAttribute("success", "Đã cập nhật tồn kho.");
            } catch (CatalogValidationException exception) {
                redirect.addFlashAttribute("error", exception.getMessage());
            }
        }
        return "redirect:/admin/inventory";
    }

    @GetMapping("/admin/books/new")
    public String create(Model model) {
        model.addAttribute("form", new BookForm());
        addReferences(model);
        return "admin/book-form";
    }

    @GetMapping("/admin/books/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("form", bookService.getForm(id));
        addReferences(model);
        return "admin/book-form";
    }

    @PostMapping("/admin/books/save")
    public String save(@Valid @ModelAttribute("form") BookForm form, BindingResult bindingResult,
                       Model model, RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasErrors()) {
            try {
                bookService.save(form);
                redirectAttributes.addFlashAttribute("success", "Sách đã được lưu.");
                return "redirect:/admin/books";
            } catch (CatalogValidationException exception) {
                bindingResult.rejectValue(exception.getField(), "catalog", exception.getMessage());
            } catch (DataIntegrityViolationException exception) {
                bindingResult.reject("conflict", "ISBN hoặc đường dẫn vừa được sử dụng. Hãy kiểm tra lại.");
            }
        }
        addReferences(model);
        return "admin/book-form";
    }

    @PostMapping("/admin/books/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            redirect.addFlashAttribute("success", bookService.delete(id));
        } catch (DataIntegrityViolationException exception) {
            redirect.addFlashAttribute("error", "Sách vừa phát sinh liên kết. Hãy dùng thao tác ngừng bán.");
        }
        return "redirect:/admin/books";
    }

    @PostMapping("/admin/books/{id}/deactivate")
    public String deactivate(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bookService.deactivate(id);
        redirectAttributes.addFlashAttribute("success", "Sách đã được chuyển sang ngừng bán.");
        return "redirect:/admin/books";
    }

    private void addReferences(Model model) {
        model.addAttribute("categories", bookService.adminCategories());
        model.addAttribute("authors", bookService.authors());
        model.addAttribute("publishers", bookService.publishers());
        model.addAttribute("statuses", BookStatus.values());
    }
}
