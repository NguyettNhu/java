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

    private final ReferenceDataService references;

    public AdminBookController(BookService bookService, ReferenceDataService references) {
        this.bookService = bookService;
        this.references = references;
    }

    @GetMapping("/admin/books")
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) Long publisherId,
                       @RequestParam(required = false) Long authorId,
                       @RequestParam(required = false) java.math.BigDecimal minPrice,
                       @RequestParam(required = false) java.math.BigDecimal maxPrice,
                       @RequestParam(required = false) BookStatus status,
                       @RequestParam(defaultValue = "") String stock,
                       @RequestParam(defaultValue = "newest") String sort,
                       @RequestParam(defaultValue = "0") int page, Model model) {
                // Từ chối khoảng giá không hợp lệ trước khi truy vấn danh sách sách.
        if ((minPrice != null && minPrice.signum() < 0) || (maxPrice != null && maxPrice.signum() < 0)
                || (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0)) {
            model.addAttribute("books", org.springframework.data.domain.Page.empty());
            model.addAttribute("error", "Giá phải không âm và giá tối đa phải lớn hơn hoặc bằng giá tối thiểu.");
        } else {
            model.addAttribute("books", bookService.searchAdmin(
                    new BookSearchQuery(keyword, categoryId, authorId, minPrice, maxPrice, sort, publisherId), status, stock, page, 20));
        }
        model.addAttribute("keyword", keyword);
        model.addAttribute("publisherId", publisherId);
        model.addAttribute("authorId", authorId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
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
        // Cập nhật tồn kho và đưa kết quả thao tác về trang danh sách.
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
        // Lưu sách khi form hợp lệ và giữ lại form nếu nghiệp vụ bị từ chối.
        if (!bindingResult.hasErrors()) {
            try {
                bookService.save(form);
                redirectAttributes.addFlashAttribute("success", "Sách đã được lưu.");
                return "redirect:/admin/books";
            } catch (CatalogValidationException exception) {
                bindingResult.rejectValue(exception.getField(), "catalog", exception.getMessage());
            } catch (DataIntegrityViolationException exception) {
                bindingResult.reject("conflict", "ISBN, đường dẫn hoặc thể loại vừa được sử dụng. Hãy kiểm tra và lưu lại.");
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
        // Dùng dữ liệu tham chiếu đã cache thay vì ba truy vấn riêng cho mỗi lần mở trang.
        model.addAttribute("categories", references.list(ReferenceType.CATEGORIES));
        model.addAttribute("authors", references.list(ReferenceType.AUTHORS));
        model.addAttribute("publishers", references.list(ReferenceType.PUBLISHERS));
        model.addAttribute("statuses", BookStatus.values());
    }
}
