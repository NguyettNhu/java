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

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class ReportService {
    static final int RECENT_DAYS = 30;

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
                orderItemRepository.topSellingBooks(PageRequest.of(0, 5)), recentRevenue());
    }

    private AnalyticsView.Chart recentRevenue() {
        // Doanh thu đơn hoàn thành của 30 ngày gần nhất (tính cả hôm nay), mỗi ngày một điểm, ngày trống bằng 0.
        LocalDate today = LocalDate.now(ReportPeriod.ZONE);
        ReportPeriod period = new ReportPeriod(today.minusDays(RECENT_DAYS - 1), today);
        Map<LocalDate, BigDecimal> days = new TreeMap<>();
        for (LocalDate d = period.from(); !d.isAfter(period.to()); d = d.plusDays(1)) days.put(d, BigDecimal.ZERO);
        for (Object[] row : orderRepository.completedSince(period.start()))
            days.computeIfPresent(((Instant) row[0]).atZone(ReportPeriod.ZONE).toLocalDate(), (d, sum) -> sum.add((BigDecimal) row[1]));
        DateTimeFormatter label = DateTimeFormatter.ofPattern("dd/MM");
        List<AnalyticsView.Point> points = days.entrySet().stream()
                .map(e -> new AnalyticsView.Point(e.getKey().format(label), e.getValue(), null)).toList();
        return new AnalyticsView.Chart("recent-revenue", "Doanh thu 30 ngày gần nhất", "line", "₫",
                "Đơn hoàn thành, theo ngày đặt hàng (giờ Việt Nam).", points);
    }
}
