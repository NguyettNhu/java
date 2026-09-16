package com.yukihira.bookstore;

import com.yukihira.bookstore.author.AuthorRepository;
import com.yukihira.bookstore.book.*;
import com.yukihira.bookstore.category.CategoryRepository;
import com.yukihira.bookstore.config.DemoDataInitializer;
import com.yukihira.bookstore.publisher.PublisherRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DemoCatalogTests {
    @Autowired BookRepository books;
    @Autowired CategoryRepository categories;
    @Autowired AuthorRepository authors;
    @Autowired PublisherRepository publishers;

    @Test
    void canAppendToExistingCatalogAndRerunWithoutChangingPricesStockOrCreatingDuplicates() {
        var seeder = new DemoDataInitializer(books, categories, authors, publishers, true);
        seeder.run(null);
        assertThat(books.count()).isEqualTo(38);
        assertThat(categories.count()).isEqualTo(8);
        assertThat(publishers.count()).isEqualTo(6);
        var edited = books.findBySlug("mau-bat-dau-lap-trinh-java").orElseThrow();
        assertThat(edited.getImageUrl()).isEqualTo("https://covers.openlibrary.org/b/id/1094406-M.jpg");
        edited.setPrice(new BigDecimal("321000")); edited.setStock(8); books.saveAndFlush(edited);
        var authorCount = authors.count();
        seeder.run(null);
        assertThat(books.count()).isEqualTo(38); assertThat(authors.count()).isEqualTo(authorCount);
        assertThat(edited.getPrice()).isEqualByComparingTo("321000"); assertThat(edited.getStock()).isEqualTo(8);
        assertThat(books.findAll()).filteredOn(b -> b.getStock() == 0)
                .allSatisfy(b -> assertThat(b.getStatus()).isEqualTo(BookStatus.OUT_OF_STOCK));
    }
}
