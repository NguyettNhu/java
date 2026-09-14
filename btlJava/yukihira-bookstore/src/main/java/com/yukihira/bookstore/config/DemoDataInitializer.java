package com.yukihira.bookstore.config;

import com.yukihira.bookstore.author.Author;
import com.yukihira.bookstore.author.AuthorRepository;
import com.yukihira.bookstore.book.Book;
import com.yukihira.bookstore.book.BookRepository;
import com.yukihira.bookstore.book.BookStatus;
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
        if (!enabled) return;
        long before = bookRepository.count();

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
        seedExpandedCatalog();
        org.slf4j.LoggerFactory.getLogger(getClass()).info("Demo catalog: added {} books; total {} books, {} categories, {} publishers, {} authors",
                bookRepository.count() - before, bookRepository.count(), categoryRepository.count(), publisherRepository.count(), authorRepository.count());
    }

    private void seedExpandedCatalog() {
        // Original sample titles and illustrative metadata; no invented ISBNs or sales history.
        String[][] genres = {
                {"Văn học", "van-hoc", "Những câu chuyện về con người và cuộc sống."},
                {"Kỹ năng sống", "ky-nang-song", "Thực hành kỹ năng cho công việc và đời sống."},
                {"Thiếu nhi", "thieu-nhi", "Truyện và hoạt động dành cho độc giả nhỏ tuổi."},
                {"Công nghệ", "cong-nghe", "Lập trình, dữ liệu và kiến thức số."},
                {"Kinh doanh", "kinh-doanh", "Quản lý, vận hành và khởi nghiệp."},
                {"Lịch sử", "lich-su", "Khám phá lịch sử và văn hóa."},
                {"Khoa học", "khoa-hoc", "Tìm hiểu tự nhiên qua các câu hỏi gần gũi."},
                {"Du lịch", "du-lich", "Hành trình, địa danh và trải nghiệm."}
        };
        String[][] presses = {
                {"NXB mẫu Mây Sáng", "12 Nguyễn Văn Bình, Thành phố Hồ Chí Minh"},
                {"NXB mẫu Tri Thức Mới", "24 Tràng Tiền, Hà Nội"},
                {"NXB mẫu Lá Xanh", "18 Bạch Đằng, Đà Nẵng"},
                {"NXB mẫu Bình Minh", "36 Lê Lợi, Huế"},
                {"NXB mẫu Hành Trình", "42 Hòa Bình, Cần Thơ"}
        };
        String[] writers = {"An Nhiên", "Minh Khuê", "Hoàng Lâm", "Thu Giang", "Hải Đăng", "Ngọc Mai", "Thanh Sơn", "Bảo Linh", "Khánh An", "Quỳnh Chi", "Nhật Minh", "Phương Hạ"};
        String[] titles = {
                "Nhật ký bên cửa sổ", "Một buổi chiều có nắng", "Đường về mùa hạ", "Những lá thư chưa gửi",
                "Lắng nghe chính mình", "Sắp xếp một ngày bận rộn", "Học cách nói lời cảm ơn", "Ghi chép để trưởng thành",
                "Khu vườn của bạn Thỏ", "Chuyến tàu đến đảo Cầu Vồng", "Bé khám phá bốn mùa", "Chú mèo học đếm",
                "Bắt đầu lập trình Java", "Dữ liệu quanh ta", "Thiết kế ứng dụng đầu tiên", "Thực hành tư duy thuật toán",
                "Vận hành một cửa hàng nhỏ", "Kể câu chuyện thương hiệu", "Lập kế hoạch cho ý tưởng mới", "Hiểu khách hàng mỗi ngày",
                "Dạo bước qua phố cổ", "Chuyện kể từ bảo tàng", "Dấu xưa trong nếp nhà", "Những trang sử bên dòng sông",
                "Vì sao bầu trời đổi màu", "Thế giới nhỏ dưới kính lúp", "Một ngày cùng các hành tinh", "Thí nghiệm trong căn bếp",
                "Qua những miền xanh", "Cuối tuần ở thành phố lạ", "Hành trang cho chuyến đi", "Những cung đường ven biển"
        };
        int[] stocks = {48, 32, 0, 4, 76, 21, 3, 56, 92, 38, 5, 0, 65, 27, 12, 44, 81, 2, 33, 17, 24, 0, 9, 40, 58, 3, 72, 19, 31, 46, 1, 63};
        for (int i = 0; i < titles.length; i++) {
            String slug = "mau-" + com.yukihira.bookstore.common.util.Slugifier.toSlug(titles[i]);
            if (bookRepository.existsBySlug(slug)) continue;
            String[] genre = genres[i / 4];
            String[] press = presses[i % presses.length];
            saveBook(titles[i], slug, null, BigDecimal.valueOf(59000L + (i % 12) * 15000L), stocks[i],
                    category(genre[0], genre[1], genre[2]), publisher(press[0], press[1]),
                    author(writers[i % writers.length], "Tác giả minh họa trong bộ dữ liệu mẫu của nhà sách Yukihira."),
                    "Sách mẫu thuộc tủ sách " + genre[0].toLowerCase(java.util.Locale.ROOT) + ". " + genre[2]
                            + " Dữ liệu minh họa để trải nghiệm danh mục, tìm kiếm và quản lý tồn kho.");
            bookRepository.findBySlug(slug).ifPresent(b -> b.setImageUrl("/images/demo-covers/" + genre[1] + ".svg"));
        }
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
        if (bookRepository.existsBySlug(slug) || (isbn != null && bookRepository.existsByIsbn(isbn))) return;
        Book book = new Book(title, slug, price, stock, category);
        if (stock == 0) book.setStatus(BookStatus.OUT_OF_STOCK);
        book.setIsbn(isbn);
        book.setPublisher(publisher);
        book.setAuthors(new LinkedHashSet<>(List.of(author)));
        book.setDescription(description);
        bookRepository.save(book);
    }
}
