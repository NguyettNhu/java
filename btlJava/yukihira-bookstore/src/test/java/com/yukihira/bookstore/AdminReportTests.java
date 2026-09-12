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
    @Autowired ReportService reports;
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
        var report = reports.report(period);
        assertThat(report.totalOrders()).isEqualTo(4);
        assertThat(report.completedOrders()).isEqualTo(2);
        assertThat(report.completedRevenue()).isEqualByComparingTo("100000");
        assertThat(report.averageOrderValue()).isEqualByComparingTo("50000");
        assertThat(report.topSellingBooks()).singleElement().satisfies(row -> assertThat(row.quantitySold()).isEqualTo(2));
        assertThat(orderService.search(new OrderSearchQuery(user.getEmail(), null, day, day), 0, 20).getTotalElements()).isEqualTo(4);
        assertThatThrownBy(() -> new ReportPeriod(day.plusDays(1), day)).isInstanceOf(IllegalArgumentException.class);
    }

    private void create(User user, Book book, OrderStatus status, java.time.Instant created) {
        var order = new CustomerOrder("TEST-" + UUID.randomUUID().toString().substring(0, 8), user, "An", "0901234567", "Hà Nội");
        order.addItem(new OrderItem(order, book, 1));
        order.setStatus(status);
        orders.saveAndFlush(order);
        jdbc.update("update orders set created_at = ? where id = ?", Timestamp.from(created), order.getId());
    }
}
