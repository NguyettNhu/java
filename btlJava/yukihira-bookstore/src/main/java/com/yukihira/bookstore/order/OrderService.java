package com.yukihira.bookstore.order;

import com.yukihira.bookstore.book.Book;
import com.yukihira.bookstore.book.BookRepository;
import com.yukihira.bookstore.book.BookStatus;
import com.yukihira.bookstore.cart.Cart;
import com.yukihira.bookstore.cart.CartItem;
import com.yukihira.bookstore.cart.CartRepository;
import com.yukihira.bookstore.user.User;
import com.yukihira.bookstore.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final OrderTransitionPolicy transitionPolicy;

    public OrderService(OrderRepository orderRepository, CartRepository cartRepository,
                        BookRepository bookRepository, UserRepository userRepository,
                        OrderTransitionPolicy transitionPolicy) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.transitionPolicy = transitionPolicy;
    }

    @Transactional(readOnly = true)
    public CheckoutForm checkoutForm(String email) {
        User user = requireUser(email);
        CheckoutForm form = new CheckoutForm();
        form.setReceiverName(user.getFullName());
        form.setReceiverPhone(user.getPhone());
        form.setShippingAddress(user.getAddress());
        return form;
    }

    @Transactional
    public Long checkout(String email, CheckoutForm form) {
        User user = requireUser(email);
        Cart cart = cartRepository.findByUserEmailIgnoreCase(email)
                .orElseThrow(() -> new OrderException("Giỏ sách đang trống"));
        List<CartItem> cartItems = List.copyOf(cart.getItems());
        if (cartItems.isEmpty()) throw new OrderException("Giỏ sách đang trống");

        CustomerOrder order = new CustomerOrder(generateOrderCode(), user,
                clean(form.getReceiverName()), clean(form.getReceiverPhone()), clean(form.getShippingAddress()));
        order.setNote(cleanNullable(form.getNote()));
        order.setPaymentMethod(form.getPaymentMethod());

        for (CartItem cartItem : cartItems) {
            Book book = bookRepository.findForUpdate(cartItem.getBook().getId())
                    .orElseThrow(() -> new OrderException("Một cuốn sách trong giỏ không còn tồn tại"));
            if (book.getStatus() != BookStatus.ACTIVE) {
                throw new OrderException("Sách “" + book.getTitle() + "” hiện không được mở bán");
            }
            if (book.getStock() < cartItem.getQuantity()) {
                throw new OrderException("Sách “" + book.getTitle() + "” chỉ còn "
                        + book.getStock() + " cuốn trong kho");
            }
            book.decreaseStock(cartItem.getQuantity());
            order.addItem(new OrderItem(order, book, cartItem.getQuantity()));
        }

        CustomerOrder saved = orderRepository.save(order);
        cart.clear();
        return saved.getId();
    }

    @Transactional(readOnly = true)
    public List<OrderSummaryView> customerOrders(String email) {
        return orderRepository.findByUserEmailIgnoreCaseOrderByCreatedAtDesc(email).stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderDetailView customerOrder(String email, Long orderId) {
        CustomerOrder order = orderRepository.findOwnedDetailedById(orderId, email)
                .orElseThrow(() -> new OrderException("Không tìm thấy đơn hàng của bạn"));
        return toDetail(order);
    }

    @Transactional
    public void cancelByCustomer(String email, Long orderId) {
        CustomerOrder order = orderRepository.findOwnedDetailedById(orderId, email)
                .orElseThrow(() -> new OrderException("Không tìm thấy đơn hàng của bạn"));
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderException("Chỉ có thể hủy đơn đang chờ xác nhận");
        }
        transition(order, OrderStatus.CANCELLED);
    }

    @Transactional(readOnly = true)
    public Page<OrderSummaryView> search(OrderSearchQuery query, int page, int size) {
        PageRequest pageable = PageRequest.of(Math.max(page, 0), size,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return orderRepository.findAll(OrderSpecifications.from(query), pageable).map(this::toSummary);
    }

    @Transactional(readOnly = true)
    public OrderDetailView adminOrder(Long orderId) {
        CustomerOrder order = orderRepository.findDetailedById(orderId)
                .orElseThrow(() -> new OrderException("Không tìm thấy đơn hàng"));
        return toDetail(order);
    }

    @Transactional
    public void updateStatus(Long orderId, OrderStatus target) {
        CustomerOrder order = orderRepository.findDetailedById(orderId)
                .orElseThrow(() -> new OrderException("Không tìm thấy đơn hàng"));
        transition(order, target);
    }

    private void transition(CustomerOrder order, OrderStatus target) {
        transitionPolicy.validate(order.getStatus(), target);
        if (target == OrderStatus.CANCELLED) restoreStock(order);
        order.setStatus(target);
        if (target == OrderStatus.COMPLETED) order.setPaymentStatus(PaymentStatus.PAID);
        if (target == OrderStatus.CANCELLED && order.getPaymentStatus() == PaymentStatus.PAID) {
            order.setPaymentStatus(PaymentStatus.REFUNDED);
        }
    }

    private void restoreStock(CustomerOrder order) {
        if (order.isStockRestored()) return;
        for (OrderItem item : order.getItems()) {
            Book book = bookRepository.findForUpdate(item.getBook().getId())
                    .orElseThrow(() -> new OrderException("Không thể hoàn kho cho sách đã bị xóa"));
            book.increaseStock(item.getQuantity());
        }
        order.setStockRestored(true);
    }

    private User requireUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new OrderException("Không tìm thấy tài khoản"));
    }

    private OrderSummaryView toSummary(CustomerOrder order) {
        int itemCount = order.getItems().stream().mapToInt(OrderItem::getQuantity).sum();
        return new OrderSummaryView(order.getId(), order.getOrderCode(), order.getUser().getFullName(),
                order.getUser().getEmail(), order.getCreatedAt(), order.getTotalAmount(), itemCount,
                order.getStatus(), order.getPaymentStatus());
    }

    private OrderDetailView toDetail(CustomerOrder order) {
        List<OrderItemView> items = order.getItems().stream()
                .map(item -> new OrderItemView(item.getBook().getId(), item.getBookTitle(),
                        item.getBook().getSlug(), item.getBook().getImageUrl(), item.getUnitPrice(),
                        item.getQuantity(), item.getSubtotal()))
                .toList();
        return new OrderDetailView(order.getId(), order.getOrderCode(), order.getUser().getFullName(),
                order.getUser().getEmail(), order.getReceiverName(), order.getReceiverPhone(),
                order.getShippingAddress(), order.getNote(), order.getTotalAmount(), order.getPaymentMethod(),
                order.getPaymentStatus(), order.getStatus(), order.getCreatedAt(), items,
                transitionPolicy.allowedFrom(order.getStatus()), order.getStatus() == OrderStatus.PENDING);
    }

    private String generateOrderCode() {
        String date = LocalDate.now(ZoneOffset.UTC).format(DateTimeFormatter.BASIC_ISO_DATE);
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        return "YKH-" + date + "-" + random;
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private String cleanNullable(String value) {
        String cleaned = clean(value);
        return cleaned.isEmpty() ? null : cleaned;
    }
}
