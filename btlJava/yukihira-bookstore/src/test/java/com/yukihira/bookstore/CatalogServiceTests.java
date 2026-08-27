package com.yukihira.bookstore;

import com.yukihira.bookstore.admin.catalog.ReferenceDataService;
import com.yukihira.bookstore.admin.catalog.ReferenceForm;
import com.yukihira.bookstore.admin.catalog.ReferenceType;
import com.yukihira.bookstore.author.Author;
import com.yukihira.bookstore.author.AuthorRepository;
import com.yukihira.bookstore.book.BookForm;
import com.yukihira.bookstore.book.BookSearchQuery;
import com.yukihira.bookstore.book.BookService;
import com.yukihira.bookstore.book.BookStatus;
import com.yukihira.bookstore.category.Category;
import com.yukihira.bookstore.category.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CatalogServiceTests {

    @Autowired ReferenceDataService referenceDataService;
    @Autowired CategoryRepository categoryRepository;
    @Autowired AuthorRepository authorRepository;
    @Autowired BookService bookService;

    @Test
    void createsSearchesAndDeactivatesBook() {
        ReferenceForm categoryForm = new ReferenceForm();
        categoryForm.setName("Kỹ năng sống");
        categoryForm.setDetails("Sách giúp sống tốt hơn");
        categoryForm.setActive(true);
        referenceDataService.save(ReferenceType.CATEGORIES, categoryForm);
        Category category = categoryRepository.findBySlug("ky-nang-song").orElseThrow();
        Author author = authorRepository.save(new Author("James Clear"));

        BookForm form = new BookForm();
        form.setTitle("Thói quen nguyên tử");
        form.setIsbn("9786049999999");
        form.setDescription("Thay đổi nhỏ, kết quả lớn.");
        form.setPrice(new BigDecimal("189000"));
        form.setStock(12);
        form.setCategoryId(category.getId());
        form.setAuthorIds(Set.of(author.getId()));
        form.setStatus(BookStatus.ACTIVE);
        var saved = bookService.save(form);

        var result = bookService.search(new BookSearchQuery("nguyên tử", category.getId(),
                author.getId(), new BigDecimal("100000"), new BigDecimal("200000"), "price-asc"), 0, 12, true);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().authorLine()).isEqualTo("James Clear");
        bookService.deactivate(saved.getId());
        assertThat(bookService.search(new BookSearchQuery(null, null, null, null, null, "newest"),
                0, 12, true)).isEmpty();
    }
}
