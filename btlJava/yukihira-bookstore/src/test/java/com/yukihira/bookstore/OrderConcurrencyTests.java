package com.yukihira.bookstore;

import com.yukihira.bookstore.book.*;
import com.yukihira.bookstore.cart.*;
import com.yukihira.bookstore.category.*;
import com.yukihira.bookstore.order.*;
import com.yukihira.bookstore.user.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class OrderConcurrencyTests {
    @Autowired OrderService orders;
    @Autowired CartService carts;
    @Autowired CartRepository cartRepository;
    @Autowired UserRepository users;
    @Autowired CategoryRepository categories;
    @Autowired BookRepository books;

    @Test
    void simultaneousCancellationRestoresStockOnlyOnce() throws Exception {
        String token = UUID.randomUUID().toString();
        var customer = users.save(new User("Khách", token + "@example.test", "encoded"));
        cartRepository.save(new Cart(customer));
        var category = categories.save(new Category("Kệ " + token, "ke-" + token));
        var book = books.save(new Book("Sách", "sach-" + token, new BigDecimal("100000"), 5, category));
        carts.add(customer.getEmail(), book.getId(), 2);
        var form = new CheckoutForm();
        form.setReceiverName("Nguyễn An");
        form.setReceiverPhone("0901234567");
        form.setShippingAddress("123 Đường Sách, Hà Nội");
        Long id = orders.checkout(customer.getEmail(), form);
        var ready = new CountDownLatch(2);
        var go = new CountDownLatch(1);
        Callable<Boolean> cancel = () -> {
            ready.countDown();
            if (!go.await(5, TimeUnit.SECONDS)) throw new TimeoutException();
            try {
                orders.updateStatus(id, OrderStatus.CANCELLED);
                return true;
            } catch (OrderException exception) {
                return false;
            }
        };
        try (var executor = Executors.newFixedThreadPool(2)) {
            var first = executor.submit(cancel);
            var second = executor.submit(cancel);
            assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
            go.countDown();
            assertThat((first.get(10, TimeUnit.SECONDS) ? 1 : 0) + (second.get(10, TimeUnit.SECONDS) ? 1 : 0)).isEqualTo(1);
        }
        assertThat(books.findById(book.getId()).orElseThrow().getStock()).isEqualTo(5);
        assertThat(orders.adminOrder(id).status()).isEqualTo(OrderStatus.CANCELLED);
    }
}
