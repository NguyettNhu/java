package com.yukihira.bookstore.api;

import com.yukihira.bookstore.book.BookSearchQuery;
import com.yukihira.bookstore.book.BookService;
import com.yukihira.bookstore.book.BookView;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class CatalogRestController {

    private final BookService bookService;

    public CatalogRestController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/books")
    public ApiPage<BookView> books(@RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) Long categoryId,
                                   @RequestParam(required = false) Long authorId,
                                   @RequestParam(required = false) Long publisherId,
                                   @RequestParam(required = false) @NumberFormat BigDecimal minPrice,
                                   @RequestParam(required = false) @NumberFormat BigDecimal maxPrice,
                                   @RequestParam(defaultValue = "newest") String sort,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "12") int size) {
        var query = new BookSearchQuery(keyword, categoryId, authorId, minPrice, maxPrice, sort, publisherId);
        return ApiPage.from(bookService.search(query, page, size, true));
    }

    @GetMapping("/books/{slug}")
    public BookView book(@PathVariable String slug) {
        return bookService.findActiveBySlug(slug);
    }

    @GetMapping("/categories")
    public List<ReferenceResponse> categories() {
        return bookService.categories().stream()
                .map(item -> new ReferenceResponse(item.getId(), item.getName(), item.getSlug(),
                        item.getDescription(), item.isActive()))
                .toList();
    }

    @GetMapping("/authors")
    public List<ReferenceResponse> authors() {
        return bookService.authors().stream()
                .map(item -> new ReferenceResponse(item.getId(), item.getName(), null,
                        item.getBiography(), null))
                .toList();
    }

    @GetMapping("/publishers")
    public List<ReferenceResponse> publishers() {
        return bookService.publishers().stream()
                .map(item -> new ReferenceResponse(item.getId(), item.getName(), null,
                        item.getAddress(), null))
                .toList();
    }
}
