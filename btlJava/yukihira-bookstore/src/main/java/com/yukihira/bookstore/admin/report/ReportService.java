package com.yukihira.bookstore.admin.report;

import com.yukihira.bookstore.book.BookRepository;
import com.yukihira.bookstore.book.BookStatus;
import com.yukihira.bookstore.order.OrderItemRepository;
import com.yukihira.bookstore.order.OrderRepository;
import com.yukihira.bookstore.order.OrderStatus;
import com.yukihira.bookstore.user.Role;
import com.yukihira.bookstore.user.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public ReportService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                         UserRepository userRepository, BookRepository bookRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public DashboardView dashboard() {
        // Tính các chỉ số tổng quan và top sách bán chạy cho dashboard.
        return new DashboardView(orderRepository.count(), orderRepository.countByStatus(OrderStatus.PENDING),
                orderRepository.completedRevenue(), userRepository.countByRole(Role.CUSTOMER),
                bookRepository.countByStatus(BookStatus.ACTIVE),
                orderItemRepository.topSellingBooks(PageRequest.of(0, 5)));
    }

    @Transactional(readOnly = true)
    public ReportView report(ReportPeriod period) {
        // Gom tổng đơn theo trạng thái và lấy top sách trong khoảng thời gian đã chọn.
        var totals = orderRepository.reportTotals(period.start(), period.endExclusive());
        var byStatus = totals.stream().collect(java.util.stream.Collectors.toMap(OrderStatusTotal::status, row -> row));
        var rows = java.util.Arrays.stream(OrderStatus.values()).map(status -> byStatus.getOrDefault(status,
                new OrderStatusTotal(status, 0L, java.math.BigDecimal.ZERO))).toList();
        return new ReportView(period, rows,
                orderItemRepository.topSellingBooksInPeriod(period.start(), period.endExclusive(), PageRequest.of(0, 10)));
    }
}
