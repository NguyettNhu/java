package com.yukihira.bookstore.cart;

import com.yukihira.bookstore.book.Book;
import com.yukihira.bookstore.book.BookRepository;
import com.yukihira.bookstore.book.BookStatus;
import com.yukihira.bookstore.user.User;
import com.yukihira.bookstore.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                       BookRepository bookRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public CartView getCart(String email) {
        return cartRepository.findByUserEmailIgnoreCase(email)
                .map(this::toView)
                .orElseGet(() -> new CartView(List.of(), BigDecimal.ZERO));
    }

    @Transactional
    public void add(String email, Long bookId, int quantity) {
        if (quantity <= 0) throw new CartException("Số lượng phải lớn hơn 0");
        Book book = availableBook(bookId);
        Cart cart = getOrCreateCart(email);
        CartItem item = cartItemRepository.findByCartIdAndBookId(cart.getId(), bookId).orElse(null);
        int nextQuantity = quantity + (item == null ? 0 : item.getQuantity());
        validateStock(book, nextQuantity);
        if (item == null) {
            CartItem newItem = new CartItem(cart, book, quantity);
            cart.addItem(newItem);
            cartItemRepository.save(newItem);
        } else {
            item.setQuantity(nextQuantity);
        }
    }

    @Transactional
    public void update(String email, Long itemId, int quantity) {
        if (quantity <= 0) throw new CartException("Số lượng phải lớn hơn 0");
        CartItem item = ownedItem(email, itemId);
        validateStock(item.getBook(), quantity);
        item.setQuantity(quantity);
    }

    @Transactional
    public void remove(String email, Long itemId) {
        CartItem item = ownedItem(email, itemId);
        item.getCart().removeItem(item);
        cartItemRepository.delete(item);
    }

    private Cart getOrCreateCart(String email) {
        return cartRepository.findByUserEmailIgnoreCase(email).orElseGet(() -> {
            User user = userRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new CartException("Không tìm thấy tài khoản"));
            return cartRepository.save(new Cart(user));
        });
    }

    private CartItem ownedItem(String email, Long itemId) {
        return cartItemRepository.findByIdAndCartUserEmailIgnoreCase(itemId, email)
                .orElseThrow(() -> new CartException("Sản phẩm không thuộc giỏ hàng của bạn"));
    }

    private Book availableBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CartException("Không tìm thấy sách"));
        if (book.getStatus() != BookStatus.ACTIVE) {
            throw new CartException("Sách hiện không được mở bán");
        }
        return book;
    }

    private void validateStock(Book book, int quantity) {
        if (quantity > book.getStock()) {
            throw new CartException("Chỉ còn " + book.getStock() + " cuốn trong kho");
        }
    }

    private CartView toView(Cart cart) {
        List<CartItemView> items = cart.getItems().stream()
                .sorted(Comparator.comparing(item -> item.getBook().getTitle()))
                .map(item -> new CartItemView(item.getId(), item.getBook().getId(), item.getBook().getTitle(),
                        item.getBook().getSlug(), item.getBook().getImageUrl(), item.getBook().getPrice(),
                        item.getQuantity(), item.getBook().getStock(), item.getBook().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()))))
                .toList();
        BigDecimal total = items.stream().map(CartItemView::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartView(items, total);
    }
}
