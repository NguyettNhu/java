package com.yukihira.bookstore;

import com.yukihira.bookstore.book.Book;
import com.yukihira.bookstore.book.BookRepository;
import com.yukihira.bookstore.cart.Cart;
import com.yukihira.bookstore.cart.CartRepository;
import com.yukihira.bookstore.cart.CartService;
import com.yukihira.bookstore.category.Category;
import com.yukihira.bookstore.category.CategoryRepository;
import com.yukihira.bookstore.order.CheckoutForm;
import com.yukihira.bookstore.order.OrderException;
import com.yukihira.bookstore.order.OrderRepository;
import com.yukihira.bookstore.order.OrderService;
import com.yukihira.bookstore.order.OrderStatus;
import com.yukihira.bookstore.order.PaymentMethod;
import com.yukihira.bookstore.user.User;
import com.yukihira.bookstore.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class OrderServiceTests {

    @Autowired OrderService orderService;
    @Autowired CartService cartService;
    @Autowired OrderRepository orderRepository;
    @Autowired UserRepository userRepository;
    @Autowired CartRepository cartRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired BookRepository bookRepository;

    @Test
    void checkoutUsesDatabasePriceDecreasesStockAndKeepsSnapshot() {
        Fixture fixture = fixture(5);
        cartService.add(fixture.email(), fixture.bookId(), 2);

        Long orderId = orderService.checkout(fixture.email(), checkoutForm());
        var detail = orderService.customerOrder(fixture.email(), orderId);

        assertThat(detail.totalAmount()).isEqualByComparingTo("240000");
        assertThat(detail.items()).singleElement().satisfies(item -> {
            assertThat(item.unitPrice()).isEqualByComparingTo("120000");
            assertThat(item.quantity()).isEqualTo(2);
        });
        assertThat(bookRepository.findById(fixture.bookId()).orElseThrow().getStock()).isEqualTo(3);
        assertThat(cartService.getCart(fixture.email()).items()).isEmpty();

        Book changedBook = bookRepository.findById(fixture.bookId()).orElseThrow();
        changedBook.setPrice(new BigDecimal("999000"));
        bookRepository.save(changedBook);
        assertThat(orderService.customerOrder(fixture.email(), orderId).items().getFirst().unitPrice())
                .isEqualByComparingTo("120000");
    }

    @Test
    void staleCartWithInsufficientStockDoesNotCreateOrderOrClearCart() {
        Fixture fixture = fixture(5);
        cartService.add(fixture.email(), fixture.bookId(), 5);
        Book changedBook = bookRepository.findById(fixture.bookId()).orElseThrow();
        changedBook.setStock(1);
        bookRepository.save(changedBook);
        long orderCount = orderRepository.count();

        assertThatThrownBy(() -> orderService.checkout(fixture.email(), checkoutForm()))
                .isInstanceOf(OrderException.class).hasMessageContaining("chỉ còn 1");

        assertThat(orderRepository.count()).isEqualTo(orderCount);
        assertThat(bookRepository.findById(fixture.bookId()).orElseThrow().getStock()).isEqualTo(1);
        assertThat(cartService.getCart(fixture.email()).itemCount()).isEqualTo(5);
    }

    @Test
    void customerCannotReadAnotherCustomersOrder() {
        Fixture owner = fixture(3);
        cartService.add(owner.email(), owner.bookId(), 1);
        Long orderId = orderService.checkout(owner.email(), checkoutForm());
        Fixture stranger = fixture(2);

        assertThatThrownBy(() -> orderService.customerOrder(stranger.email(), orderId))
                .isInstanceOf(OrderException.class).hasMessageContaining("của bạn");
    }

    @Test
    void transitionPolicyRejectsSkippingStepsAndCancellationRestoresStockOnce() {
        Fixture fixture = fixture(5);
        cartService.add(fixture.email(), fixture.bookId(), 2);
        Long orderId = orderService.checkout(fixture.email(), checkoutForm());

        assertThatThrownBy(() -> orderService.updateStatus(orderId, OrderStatus.SHIPPING))
                .isInstanceOf(OrderException.class).hasMessageContaining("Không thể chuyển");

        orderService.cancelByCustomer(fixture.email(), orderId);
        assertThat(orderService.customerOrder(fixture.email(), orderId).status()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(bookRepository.findById(fixture.bookId()).orElseThrow().getStock()).isEqualTo(5);

        assertThatThrownBy(() -> orderService.cancelByCustomer(fixture.email(), orderId))
                .isInstanceOf(OrderException.class).hasMessageContaining("Chỉ có thể hủy");
        assertThat(bookRepository.findById(fixture.bookId()).orElseThrow().getStock()).isEqualTo(5);
    }

    private Fixture fixture(int stock) {
        String token = UUID.randomUUID().toString();
        String email = "reader-" + token + "@example.com";
        User user = userRepository.save(new User("Độc giả Yukihira", email, "encoded"));
        cartRepository.save(new Cart(user));
        Category category = categoryRepository.save(new Category("Thể loại " + token, "the-loai-" + token));
        Book book = bookRepository.save(new Book("Cuốn sách " + token, "cuon-sach-" + token,
                new BigDecimal("120000"), stock, category));
        return new Fixture(email, book.getId());
    }

    private CheckoutForm checkoutForm() {
        CheckoutForm form = new CheckoutForm();
        form.setReceiverName("Nguyễn An");
        form.setReceiverPhone("0901234567");
        form.setShippingAddress("123 Đường Sách, TP.HCM");
        form.setPaymentMethod(PaymentMethod.COD);
        return form;
    }

    private record Fixture(String email, Long bookId) {
    }
}
