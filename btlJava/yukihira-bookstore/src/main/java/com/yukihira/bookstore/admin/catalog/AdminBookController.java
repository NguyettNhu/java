package com.yukihira.bookstore.admin.catalog;

import com.yukihira.bookstore.book.BookForm;
import com.yukihira.bookstore.book.BookSearchQuery;
import com.yukihira.bookstore.book.BookService;
import com.yukihira.bookstore.book.BookStatus;
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
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("books", bookService.search(
                new BookSearchQuery(keyword, null, null, null, null, "newest"), page, 20, false));
        model.addAttribute("keyword", keyword);
        return "admin/book-list";
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
            } catch (IllegalArgumentException exception) {
                bindingResult.rejectValue("isbn", "duplicate", exception.getMessage());
            }
        }
        addReferences(model);
        return "admin/book-form";
    }

    @PostMapping("/admin/books/{id}/deactivate")
    public String deactivate(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bookService.deactivate(id);
        redirectAttributes.addFlashAttribute("success", "Sách đã được chuyển sang ngừng bán.");
        return "redirect:/admin/books";
    }

    private void addReferences(Model model) {
        model.addAttribute("categories", bookService.categories());
        model.addAttribute("authors", bookService.authors());
        model.addAttribute("publishers", bookService.publishers());
        model.addAttribute("statuses", BookStatus.values());
    }
}
