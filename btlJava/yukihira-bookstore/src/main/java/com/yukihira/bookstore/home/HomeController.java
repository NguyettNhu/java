package com.yukihira.bookstore.home;

import com.yukihira.bookstore.book.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final BookService bookService;

    public HomeController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featuredBooks", bookService.featured());
        model.addAttribute("categories", bookService.categories());
        return "home";
    }
}
