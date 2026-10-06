package com.yukihira.bookstore.admin.catalog;

import com.yukihira.bookstore.admin.Facet;
import com.yukihira.bookstore.admin.report.AdminSummaryService;
import com.yukihira.bookstore.admin.report.AdminSummaryService.BookSummary;
import com.yukihira.bookstore.book.BookForm;
import com.yukihira.bookstore.book.BookSearchQuery;
import com.yukihira.bookstore.book.BookService;
import com.yukihira.bookstore.book.BookStatus;
import com.yukihira.bookstore.book.StockForm;
import org.springframework.dao.DataIntegrityViolationException;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AdminBookController {

    private final BookService bookService;

    private final ReferenceDataService references;

    private final AdminSummaryService summaries;

    public AdminBookController(BookService bookService, ReferenceDataService references, AdminSummaryService summaries) {
        this.bookService = bookService;
        this.references = references;
        this.summaries = summaries;
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
                       @RequestParam(defaultValue = "0") int page, HttpServletRequest request, Model model) {
                // Từ chối khoảng giá không hợp lệ trước khi truy vấn danh sách sách.
        if ((minPrice != null && minPrice.signum() < 0) || (maxPrice != null && maxPrice.signum() < 0)
                || (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0)) {
            model.addAttribute("books", org.springframework.data.domain.Page.empty());
            model.addAttribute("error", "Giá phải không âm và giá tối đa phải lớn hơn hoặc bằng giá tối thiểu.");
        } else {
            var query = new BookSearchQuery(keyword, categoryId, authorId, minPrice, maxPrice, sort, publisherId);
            model.addAttribute("books", bookService.searchAdmin(query, status, stock, page, 20));
            BookSummary summary = summaries.books(query, status, stock, false);
            model.addAttribute("summary", summary);
            model.addAttribute("statusFacets", List.of(
                    Facet.of(request, "status", null, "Mọi trạng thái", summary.statusTotal()),
                    Facet.of(request, "status", BookStatus.ACTIVE.name(), BookStatus.ACTIVE.getLabel(), summary.count(BookStatus.ACTIVE)),
                    Facet.of(request, "status", BookStatus.OUT_OF_STOCK.name(), BookStatus.OUT_OF_STOCK.getLabel(), summary.count(BookStatus.OUT_OF_STOCK)),
                    Facet.of(request, "status", BookStatus.INACTIVE.name(), BookStatus.INACTIVE.getLabel(), summary.count(BookStatus.INACTIVE))));
            model.addAttribute("stockFacets", stockFacets(request, summary));
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
        model.addAttribute("sales", summaries.bookSales(id));
        return "admin/book-detail";
    }

    @GetMapping("/admin/inventory")
    public String inventory(@RequestParam(required = false) String keyword,
                            @RequestParam(defaultValue = "") String stock,
                            @RequestParam(defaultValue = "0") int page, HttpServletRequest request, Model model) {
        var query = new BookSearchQuery(keyword, null, null, null, null, "title");
        model.addAttribute("books", bookService.searchAdmin(query, null, stock, page, 20));
        BookSummary summary = summaries.books(query, null, stock, true);
        model.addAttribute("summary", summary);
        model.addAttribute("stockFacets", stockFacets(request, summary));
        model.addAttribute("keyword", keyword);
        model.addAttribute("stockFilter", stock);
        return "admin/inventory";
    }

    /** Lối tắt mức tồn kho; số đếm giữ nguyên các bộ lọc khác của trang. */
    private static List<Facet> stockFacets(HttpServletRequest request, BookSummary summary) {
        return List.of(Facet.of(request, "stock", null, "Mọi mức tồn", summary.levelTotal()),
                Facet.of(request, "stock", "low", "Sắp hết (1–5 cuốn)", summary.lowStock()),
                Facet.of(request, "stock", "empty", "Còn 0 cuốn", summary.emptyStock()));
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
