package com.yukihira.bookstore;

import com.yukihira.bookstore.book.Book;
import com.yukihira.bookstore.book.BookRepository;
import com.yukihira.bookstore.cart.Cart;
import com.yukihira.bookstore.cart.CartException;
import com.yukihira.bookstore.cart.CartRepository;
import com.yukihira.bookstore.cart.CartService;
import com.yukihira.bookstore.category.Category;
import com.yukihira.bookstore.category.CategoryRepository;
import com.yukihira.bookstore.user.User;
import com.yukihira.bookstore.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CartServiceTests {

    @Autowired CartService cartService;
    @Autowired UserRepository userRepository;
    @Autowired CartRepository cartRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired BookRepository bookRepository;

    @Test
    void addsAccumulatesUpdatesAndRemovesOwnedItem() {
        Fixture fixture = fixture("reader@example.com");
        cartService.add(fixture.email(), fixture.book().getId(), 2);
        cartService.add(fixture.email(), fixture.book().getId(), 1);

        var cart = cartService.getCart(fixture.email());
        assertThat(cart.itemCount()).isEqualTo(3);
        assertThat(cart.total()).isEqualByComparingTo("360000");

        Long itemId = cart.items().getFirst().id();
        cartService.update(fixture.email(), itemId, 4);
        assertThat(cartService.getCart(fixture.email()).total()).isEqualByComparingTo("480000");
        cartService.remove(fixture.email(), itemId);
        assertThat(cartService.getCart(fixture.email()).items()).isEmpty();
    }

    @Test
    void rejectsAnotherUsersItemAndQuantityAboveStock() {
        Fixture owner = fixture("owner@example.com");
        User stranger = userRepository.save(new User("Stranger", "stranger@example.com", "encoded"));
        cartRepository.save(new Cart(stranger));
        cartService.add(owner.email(), owner.book().getId(), 1);
        Long itemId = cartService.getCart(owner.email()).items().getFirst().id();

        assertThatThrownBy(() -> cartService.update("stranger@example.com", itemId, 2))
                .isInstanceOf(CartException.class).hasMessageContaining("không thuộc");
        assertThatThrownBy(() -> cartService.add(owner.email(), owner.book().getId(), 99))
                .isInstanceOf(CartException.class).hasMessageContaining("Chỉ còn");
    }

    private Fixture fixture(String email) {
        User user = userRepository.save(new User("Reader", email, "encoded"));
        cartRepository.save(new Cart(user));
        Category category = categoryRepository.save(new Category("Tiểu thuyết " + email, "tieu-thuyet-" + user.getId()));
        Book book = bookRepository.save(new Book("Một cuốn sách " + email, "mot-cuon-" + user.getId(),
                new BigDecimal("120000"), 8, category));
        return new Fixture(email, book);
    }

    private record Fixture(String email, Book book) {
    }
}
