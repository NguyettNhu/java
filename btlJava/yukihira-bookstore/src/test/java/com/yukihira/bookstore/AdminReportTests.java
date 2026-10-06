package com.yukihira.bookstore;

import com.yukihira.bookstore.admin.report.*;
import com.yukihira.bookstore.book.*;
import com.yukihira.bookstore.category.*;
import com.yukihira.bookstore.order.*;
import com.yukihira.bookstore.user.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminReportTests {
    @Autowired AnalyticsService analytics;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orders;
    @Autowired UserRepository users;
    @Autowired CategoryRepository categories;
    @Autowired BookRepository books;
    @Autowired JdbcTemplate jdbc;

    @Test
    void reportsAndOrderFiltersUseVietnameseDayBoundariesAndCompletedOrders() {
        var day = LocalDate.of(2024, 2, 5);
        var period = new ReportPeriod(day, day);
        var token = UUID.randomUUID().toString();
        var user = users.save(new User("Khách", token + "@example.test", "encoded"));
        var category = categories.save(new Category("Kệ " + token, "ke-" + token));
        var book = books.save(new Book("Sách báo cáo", "report-" + token, new BigDecimal("50000"), 10, category));
        create(user, book, OrderStatus.COMPLETED, period.start());
        create(user, book, OrderStatus.COMPLETED, period.endExclusive().minusSeconds(1));
        create(user, book, OrderStatus.PENDING, period.start().plusSeconds(60));
        create(user, book, OrderStatus.CANCELLED, period.start().plusSeconds(120));
        create(user, book, OrderStatus.COMPLETED, period.endExclusive());
        var report = analytics.build(AnalyticsFilter.resolve("day", day, null, null, null, null, category.getId(), null));
        assertThat(report.totalOrders()).isEqualTo(4);
        assertThat(report.completedOrders()).isEqualTo(2);
        assertThat(report.revenue()).isEqualByComparingTo("100000");
        assertThat(report.averageOrderValue()).isEqualByComparingTo("50000");
        assertThat(report.chart("top-books").points()).singleElement().satisfies(row -> assertThat(row.value()).isEqualByComparingTo("2"));
        // Bảng trạng thái đếm mọi đơn trong kỳ và cộng tiền theo từng trạng thái.
        assertThat(report.statusTotals()).hasSize(OrderStatus.values().length);
        assertThat(report.statusTotals()).filteredOn(s -> s.status() == OrderStatus.COMPLETED).singleElement()
                .satisfies(s -> { assertThat(s.count()).isEqualTo(2); assertThat(s.amount()).isEqualByComparingTo("100000"); });
        assertThat(report.statusTotals()).filteredOn(s -> s.status() == OrderStatus.CANCELLED).singleElement()
                .satisfies(s -> assertThat(report.statusShare(s)).isEqualByComparingTo("25.0"));
        assertThat(orderService.search(new OrderSearchQuery(user.getEmail(), null, day, day), 0, 20).getTotalElements()).isEqualTo(4);
        assertThatThrownBy(() -> new ReportPeriod(day.plusDays(1), day)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void reportComparesWithPreviousCalendarPeriodAndSkipsPercentWhenPreviousIsEmpty() {
        var token = UUID.randomUUID().toString();
        var user = users.save(new User("Khách", token + "@example.test", "encoded"));
        var category = categories.save(new Category("Kệ " + token, "ke-" + token));
        var book = books.save(new Book("Sách so sánh", "compare-" + token, new BigDecimal("10000"), 10, category));
        // Tháng 3/2024 so với tháng 2/2024 (29 ngày), không lùi 31 ngày.
        create(user, book, OrderStatus.COMPLETED, new ReportPeriod(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 1)).start());
        create(user, book, OrderStatus.COMPLETED, new ReportPeriod(LocalDate.of(2024, 3, 10), LocalDate.of(2024, 3, 10)).start());
        create(user, book, OrderStatus.COMPLETED, new ReportPeriod(LocalDate.of(2024, 3, 11), LocalDate.of(2024, 3, 11)).start());
        var march = analytics.build(AnalyticsFilter.resolve("month", LocalDate.of(2024, 3, 15), null, null, null, null, category.getId(), null));
        assertThat(march.previous().range()).isEqualTo(new ReportPeriod(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 29)));
        assertThat(march.previous().revenue()).isEqualByComparingTo("10000");
        assertThat(march.revenueChange()).isEqualByComparingTo("100.0");
        assertThat(march.ordersChange()).isEqualByComparingTo("100.0");
        var custom = AnalyticsFilter.resolve("custom", null, LocalDate.of(2024, 3, 10), LocalDate.of(2024, 3, 16), null, null, null, null);
        assertThat(custom.previous().range()).isEqualTo(new ReportPeriod(LocalDate.of(2024, 3, 3), LocalDate.of(2024, 3, 9)));
        var january = analytics.build(AnalyticsFilter.resolve("month", LocalDate.of(2024, 1, 15), null, null, null, null, category.getId(), null));
        assertThat(january.revenueChange()).isNull();
    }

    @Test
    void reportShowsRevenueByAllThreeCatalogDimensions() {
        var view = analytics.build(AnalyticsFilter.resolve("month", LocalDate.of(2024, 3, 15), null, null, null, null, null, null));
        assertThat(view.chart("revenue-by-categories")).isNotNull();
        assertThat(view.chart("revenue-by-authors")).isNotNull();
        assertThat(view.chart("revenue-by-publishers")).isNotNull();
        assertThat(view.chart("revenue-time")).isNotNull();
    }

    private void create(User user, Book book, OrderStatus status, java.time.Instant created) {
        var order = new CustomerOrder("TEST-" + UUID.randomUUID().toString().substring(0, 8), user, "An", "0901234567", "Hà Nội");
        order.addItem(new OrderItem(order, book, 1));
        order.setStatus(status);
        orders.saveAndFlush(order);
        jdbc.update("update orders set created_at = ? where id = ?", Timestamp.from(created), order.getId());
    }
}
