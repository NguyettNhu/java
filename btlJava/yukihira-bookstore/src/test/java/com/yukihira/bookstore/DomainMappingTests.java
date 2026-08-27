package com.yukihira.bookstore;

import com.yukihira.bookstore.author.Author;
import com.yukihira.bookstore.author.AuthorRepository;
import com.yukihira.bookstore.book.Book;
import com.yukihira.bookstore.book.BookRepository;
import com.yukihira.bookstore.category.Category;
import com.yukihira.bookstore.category.CategoryRepository;
import com.yukihira.bookstore.order.CustomerOrder;
import com.yukihira.bookstore.order.OrderItem;
import com.yukihira.bookstore.order.OrderRepository;
import com.yukihira.bookstore.publisher.Publisher;
import com.yukihira.bookstore.publisher.PublisherRepository;
import com.yukihira.bookstore.user.User;
import com.yukihira.bookstore.user.UserRepository;
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
class DomainMappingTests {

    @Autowired CategoryRepository categoryRepository;
    @Autowired AuthorRepository authorRepository;
    @Autowired PublisherRepository publisherRepository;
    @Autowired BookRepository bookRepository;
    @Autowired UserRepository userRepository;
    @Autowired OrderRepository orderRepository;

    @Test
    void persistsCatalogAndOrderPriceSnapshot() {
        Category category = categoryRepository.save(new Category("Văn học", "van-hoc"));
        Author author = authorRepository.save(new Author("Haruki Murakami"));
        Publisher publisher = publisherRepository.save(new Publisher("Yukihira Press"));

        Book book = new Book("Rừng Na Uy", "rung-na-uy", new BigDecimal("125000.00"), 10, category);
        book.setIsbn("9786040000001");
        book.setPublisher(publisher);
        book.setAuthors(Set.of(author));
        book = bookRepository.saveAndFlush(book);

        User user = userRepository.save(new User("Nguyễn An", "an@example.com", "encoded"));
        CustomerOrder order = new CustomerOrder("YKH-TEST-001", user, "Nguyễn An", "0900000000", "Hà Nội");
        order.addItem(new OrderItem(order, book, 2));
        order = orderRepository.saveAndFlush(order);

        assertThat(order.getTotalAmount()).isEqualByComparingTo("250000.00");
        assertThat(order.getItems().getFirst().getBookTitle()).isEqualTo("Rừng Na Uy");
        assertThat(order.getItems().getFirst().getUnitPrice()).isEqualByComparingTo("125000.00");
        assertThat(book.getVersion()).isZero();
        assertThat(book.getCreatedAt()).isNotNull();
    }
}
