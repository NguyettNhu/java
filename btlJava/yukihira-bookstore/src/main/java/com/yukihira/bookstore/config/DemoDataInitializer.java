package com.yukihira.bookstore.config;

import com.yukihira.bookstore.author.Author;
import com.yukihira.bookstore.author.AuthorRepository;
import com.yukihira.bookstore.book.Book;
import com.yukihira.bookstore.book.BookRepository;
import com.yukihira.bookstore.category.Category;
import com.yukihira.bookstore.category.CategoryRepository;
import com.yukihira.bookstore.publisher.Publisher;
import com.yukihira.bookstore.publisher.PublisherRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;

@Component
public class DemoDataInitializer implements ApplicationRunner {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final boolean enabled;

    public DemoDataInitializer(BookRepository bookRepository, CategoryRepository categoryRepository,
                               AuthorRepository authorRepository, PublisherRepository publisherRepository,
                               @Value("${APP_SEED_DEMO:false}") boolean enabled) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
        this.enabled = enabled;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!enabled || bookRepository.count() > 0) return;

        Category literature = category("Văn học", "van-hoc", "Tiểu thuyết và truyện kể chọn lọc.");
        Category skills = category("Kỹ năng sống", "ky-nang-song", "Những cuốn sách thực hành cho đời sống.");
        Category children = category("Thiếu nhi", "thieu-nhi", "Sách nuôi dưỡng trí tưởng tượng của độc giả nhỏ.");
        Publisher yukihira = publisher("Yukihira Press", "Đường Sách, Thành phố Hồ Chí Minh");
        Author murakami = author("Haruki Murakami", "Nhà văn Nhật Bản với thế giới văn chương siêu thực.");
        Author clear = author("James Clear", "Tác giả viết về thói quen và cải thiện bản thân.");
        Author exupery = author("Antoine de Saint-Exupéry", "Nhà văn và phi công người Pháp.");

        saveBook("Rừng Na Uy", "rung-na-uy", "9786040000001", new BigDecimal("168000"), 24,
                literature, yukihira, murakami, "Một câu chuyện dịu buồn về ký ức, tuổi trẻ và những điều chưa kịp nói.");
        saveBook("Kafka bên bờ biển", "kafka-ben-bo-bien", "9786040000002", new BigDecimal("198000"), 18,
                literature, yukihira, murakami, "Hành trình song song nơi hiện thực và giấc mơ đan vào nhau.");
        saveBook("Thói quen nguyên tử", "thoi-quen-nguyen-tu", "9786040000003", new BigDecimal("189000"), 30,
                skills, yukihira, clear, "Những thay đổi nhỏ tạo nên kết quả bền vững.");
        saveBook("Hoàng tử bé", "hoang-tu-be", "9786040000004", new BigDecimal("89000"), 35,
                children, yukihira, exupery, "Cuộc gặp gỡ trong trẻo dành cho độc giả ở mọi độ tuổi.");
        saveBook("Phía nam biên giới, phía tây mặt trời", "phia-nam-bien-gioi", "9786040000005",
                new BigDecimal("145000"), 14, literature, yukihira, murakami,
                "Một cuốn tiểu thuyết về lựa chọn, hoài niệm và cái giá của khát khao.");
        saveBook("Thay đổi tí hon, hiệu quả bất ngờ", "thay-doi-ti-hon", "9786040000006",
                new BigDecimal("159000"), 22, skills, yukihira, clear,
                "Cẩm nang thiết kế hệ thống thói quen dễ bắt đầu và dễ duy trì.");
    }

    private Category category(String name, String slug, String description) {
        return categoryRepository.findByNameIgnoreCase(name).orElseGet(() -> {
            Category category = new Category(name, slug);
            category.setDescription(description);
            return categoryRepository.save(category);
        });
    }

    private Publisher publisher(String name, String address) {
        return publisherRepository.findByNameIgnoreCase(name).orElseGet(() -> {
            Publisher publisher = new Publisher(name);
            publisher.setAddress(address);
            return publisherRepository.save(publisher);
        });
    }

    private Author author(String name, String biography) {
        return authorRepository.findByNameIgnoreCase(name).orElseGet(() -> {
            Author author = new Author(name);
            author.setBiography(biography);
            return authorRepository.save(author);
        });
    }

    private void saveBook(String title, String slug, String isbn, BigDecimal price, int stock,
                          Category category, Publisher publisher, Author author, String description) {
        Book book = new Book(title, slug, price, stock, category);
        book.setIsbn(isbn);
        book.setPublisher(publisher);
        book.setAuthors(new LinkedHashSet<>(List.of(author)));
        book.setDescription(description);
        bookRepository.save(book);
    }
}
