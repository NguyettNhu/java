package com.yukihira.bookstore.book;

import org.springframework.format.annotation.NumberFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/books")
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) Long authorId,
                       @RequestParam(required = false) @NumberFormat BigDecimal minPrice,
                       @RequestParam(required = false) @NumberFormat BigDecimal maxPrice,
                       @RequestParam(defaultValue = "newest") String sort,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        BookSearchQuery query = new BookSearchQuery(keyword, categoryId, authorId, minPrice, maxPrice, sort);
        model.addAttribute("books", bookService.search(query, page, 12, true));
        model.addAttribute("query", query);
        model.addAttribute("categories", bookService.categories());
        model.addAttribute("authors", bookService.authors());
        return "books/list";
    }

    @GetMapping("/books/{slug}")
    public String detail(@PathVariable String slug, Model model) {
        model.addAttribute("book", bookService.findActiveBySlug(slug));
        return "books/detail";
    }
}
