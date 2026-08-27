package com.yukihira.bookstore;

import com.yukihira.bookstore.admin.report.ReportService;
import com.yukihira.bookstore.book.Book;
import com.yukihira.bookstore.book.BookRepository;
import com.yukihira.bookstore.cart.Cart;
import com.yukihira.bookstore.cart.CartRepository;
import com.yukihira.bookstore.cart.CartService;
import com.yukihira.bookstore.category.Category;
import com.yukihira.bookstore.category.CategoryRepository;
import com.yukihira.bookstore.order.CheckoutForm;
import com.yukihira.bookstore.order.OrderService;
import com.yukihira.bookstore.order.OrderStatus;
import com.yukihira.bookstore.order.PaymentMethod;
import com.yukihira.bookstore.user.ProfileForm;
import com.yukihira.bookstore.user.User;
import com.yukihira.bookstore.user.UserRepository;
import com.yukihira.bookstore.user.UserSearchQuery;
import com.yukihira.bookstore.user.UserService;
import com.yukihira.bookstore.user.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserAndReportServiceTests {

    @Autowired UserService userService;
    @Autowired ReportService reportService;
    @Autowired OrderService orderService;
    @Autowired CartService cartService;
    @Autowired UserRepository userRepository;
    @Autowired CartRepository cartRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired BookRepository bookRepository;

    @Test
    void customerUpdatesProfileWithoutChangingIdentity() {
        Fixture fixture = fixture();
        ProfileForm form = new ProfileForm();
        form.setFullName("Nguyễn Minh An");
        form.setPhone("0912345678");
        form.setAddress("18 Nguyễn Văn Bình, TP.HCM");

        userService.updateProfile(fixture.email(), form);

        User updated = userRepository.findByEmailIgnoreCase(fixture.email()).orElseThrow();
        assertThat(updated.getFullName()).isEqualTo("Nguyễn Minh An");
        assertThat(updated.getPhone()).isEqualTo("0912345678");
        assertThat(updated.getAddress()).contains("Nguyễn Văn Bình");
    }

    @Test
    void adminSearchesAndLocksOnlyCustomerAccounts() {
        Fixture fixture = fixture();

        var result = userService.searchCustomers(new UserSearchQuery(fixture.email(), UserStatus.ACTIVE), 0, 20);
        assertThat(result.getContent()).extracting("email").contains(fixture.email());

        userService.updateCustomerStatus(fixture.userId(), UserStatus.LOCKED);
        assertThat(userRepository.findById(fixture.userId()).orElseThrow().getStatus()).isEqualTo(UserStatus.LOCKED);
    }

    @Test
    void dashboardRevenueAndRankingUseCompletedOrdersOnly() {
        var baseline = reportService.dashboard();
        Fixture fixture = fixture();
        cartService.add(fixture.email(), fixture.bookId(), 2);
        Long orderId = orderService.checkout(fixture.email(), checkoutForm());

        assertThat(reportService.dashboard().completedRevenue()).isEqualByComparingTo(baseline.completedRevenue());

        orderService.updateStatus(orderId, OrderStatus.CONFIRMED);
        orderService.updateStatus(orderId, OrderStatus.SHIPPING);
        orderService.updateStatus(orderId, OrderStatus.COMPLETED);
        var completed = reportService.dashboard();

        assertThat(completed.totalOrders()).isEqualTo(baseline.totalOrders() + 1);
        assertThat(completed.completedRevenue()).isEqualByComparingTo(
                baseline.completedRevenue().add(new BigDecimal("240000")));
        assertThat(completed.topSellingBooks()).anySatisfy(book -> {
            assertThat(book.bookId()).isEqualTo(fixture.bookId());
            assertThat(book.quantitySold()).isEqualTo(2);
        });
    }

    private Fixture fixture() {
        String token = UUID.randomUUID().toString();
        String email = "member-" + token + "@example.com";
        User user = userRepository.save(new User("Độc giả", email, "encoded"));
        cartRepository.save(new Cart(user));
        Category category = categoryRepository.save(new Category("Kệ " + token, "ke-" + token));
        Book book = bookRepository.save(new Book("Sách báo cáo " + token, "sach-bao-cao-" + token,
                new BigDecimal("120000"), 10, category));
        return new Fixture(user.getId(), email, book.getId());
    }

    private CheckoutForm checkoutForm() {
        CheckoutForm form = new CheckoutForm();
        form.setReceiverName("Nguyễn An");
        form.setReceiverPhone("0901234567");
        form.setShippingAddress("123 Đường Sách, TP.HCM");
        form.setPaymentMethod(PaymentMethod.COD);
        return form;
    }

    private record Fixture(Long userId, String email, Long bookId) {
    }
}
