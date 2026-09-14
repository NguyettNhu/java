package com.yukihira.bookstore;

import com.yukihira.bookstore.admin.report.*;
import com.yukihira.bookstore.admin.catalog.ReferenceAnalyticsSearch;
import com.yukihira.bookstore.author.*;
import com.yukihira.bookstore.book.*;
import com.yukihira.bookstore.category.*;
import com.yukihira.bookstore.order.*;
import com.yukihira.bookstore.publisher.*;
import com.yukihira.bookstore.user.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminAnalyticsTests {
    @Autowired AnalyticsService analytics;
    @Autowired OrderRepository orders;
    @Autowired UserRepository users;
    @Autowired CategoryRepository categories;
    @Autowired PublisherRepository publishers;
    @Autowired AuthorRepository authors;
    @Autowired BookRepository books;
    @Autowired JdbcTemplate jdbc;

    @ParameterizedTest
    @CsvSource({"day,2024-02-29,2024-02-29,2024-02-29", "week,2025-01-01,2024-12-30,2025-01-05",
            "month,2024-02-17,2024-02-01,2024-02-29", "quarter,2024-12-31,2024-10-01,2024-12-31", "year,2024-06-12,2024-01-01,2024-12-31"})
    void calendarPresetsIncludeBothLocalEndpointsAndExcludeAdjacentOrders(String preset, LocalDate date, LocalDate start, LocalDate end) {
        var filter = AnalyticsFilter.resolve(preset, date, null, null, "day", null, null, null);
        assertThat(filter.range()).isEqualTo(new ReportPeriod(start, end));
        var b = book("100.25", 12, null);
        var user = customer("Khách");
        create(user, OrderStatus.COMPLETED, filter.range().start().minusSeconds(1), b, 1);
        create(user, OrderStatus.COMPLETED, filter.range().start(), b, 2);
        create(user, OrderStatus.COMPLETED, filter.range().endExclusive().minusNanos(1000), b, 3);
        create(user, OrderStatus.COMPLETED, filter.range().endExclusive(), b, 1);
        create(user, OrderStatus.PENDING, filter.range().start(), b, 4);
        create(user, OrderStatus.CANCELLED, filter.range().start(), b, 5);
        var result = analytics.build(filter, "reports", null);
        assertThat(result.revenue()).isEqualByComparingTo("501.25");
        assertThat(result.totalOrders()).isEqualTo(4);
        assertThat(result.completedOrders()).isEqualTo(2);
        assertThat(result.quantitySold()).isEqualTo(5);
        assertThat(result.buyers()).isOne();
        assertThat(result.charts().getFirst().points()).hasSize((int) (end.toEpochDay() - start.toEpochDay() + 1));
        assertThat(sum(result.charts().getFirst())).isEqualByComparingTo(result.revenue());
    }

    @Test
    void everyGroupingKeepsCustomRangeTotalAndFillsEmptyBuckets() {
        var user = customer("Khách");
        var b = book("25000", 4, null);
        var from = LocalDate.of(2023, 12, 31); var to = LocalDate.of(2024, 4, 2);
        create(user, OrderStatus.COMPLETED, new ReportPeriod(from, to).start(), b, 1);
        create(user, OrderStatus.COMPLETED, new ReportPeriod(from, to).endExclusive().minusSeconds(1), b, 2);
        for (String grouping : List.of("day", "week", "month", "quarter", "year")) {
            var result = analytics.build(AnalyticsFilter.resolve("custom", null, from, to, grouping, null, null, null), "reports", null);
            assertThat(result.revenue()).isEqualByComparingTo("75000");
            assertThat(sum(result.charts().getFirst())).isEqualByComparingTo("75000");
        }
        var empty = analytics.build(AnalyticsFilter.resolve("day", LocalDate.of(2000, 1, 1), null, null, null, null, null, null), "reports", null);
        assertThat(empty.revenue()).isZero(); assertThat(empty.averageOrderValue()).isZero();
        assertThat(empty.charts().getFirst().points()).singleElement().satisfies(p -> assertThat(p.value()).isZero());
    }

    @Test
    void publisherAndBookFiltersUseHistoricalSubtotalsAndNeverMultiplyOrderCountsForCoauthors() {
        var p1 = publishers.save(new Publisher("NXB " + UUID.randomUUID()));
        var p2 = publishers.save(new Publisher("NXB " + UUID.randomUUID()));
        var first = book("100", 7, p1); var second = book("250", 20, p2);
        first.getAuthors().add(authors.save(new Author("Đồng tác giả 1")));
        first.getAuthors().add(authors.save(new Author("Đồng tác giả 2")));
        books.saveAndFlush(first);
        var user = customer("Một khách");
        var day = LocalDate.of(2024, 5, 6); var range = new ReportPeriod(day, day);
        var order = create(user, OrderStatus.COMPLETED, range.start(), first, 2);
        order.addItem(new OrderItem(order, second, 3)); orders.saveAndFlush(order);
        first.setPrice(new BigDecimal("999")); books.saveAndFlush(first);
        var all = analytics.build(AnalyticsFilter.resolve("day", day, null, null, "day", null, null, null), "reports", null);
        assertThat(all.revenue()).isEqualByComparingTo("950"); assertThat(all.completedOrders()).isOne();
        var filtered = analytics.build(AnalyticsFilter.resolve("day", day, null, null, "day", p1.getId(), null, null), "publishers", null);
        assertThat(filtered.revenue()).isEqualByComparingTo("200"); assertThat(filtered.totalOrders()).isOne();
        assertThat(filtered.stock()).isEqualTo(7); assertThat(filtered.quantitySold()).isEqualTo(2);
        assertThat(filtered.references()).singleElement().satisfies(r -> {
            assertThat(r.revenue()).isEqualByComparingTo("200"); assertThat(r.bookCount()).isOne();
        });
        var bookDetails = analytics.build(AnalyticsFilter.resolve("day", day, null, null, "day", null, null, null), "books", first.getId());
        assertThat(bookDetails.revenue()).isEqualByComparingTo("200");
        assertThat(bookDetails.totalBooks()).isOne();
        var author = analytics.build(AnalyticsFilter.resolve("day", day, null, null, "day", null, null, first.getAuthors().iterator().next().getId()), "authors", null);
        assertThat(author.revenue()).isEqualByComparingTo("200"); assertThat(author.totalOrders()).isOne();
        var noMatch = analytics.build(AnalyticsFilter.resolve("day", day, null, null, "day", p2.getId(), first.getCategory().getId(), null), "reports", null);
        assertThat(noMatch.revenue()).isZero(); assertThat(noMatch.totalBooks()).isZero();
    }

    @Test
    void topFiveRankBySpendingAndQuantitySeparatelyAndDetailOnlyIncludesSelectedCustomer() {
        var day = LocalDate.of(2024, 8, 20); var time = new ReportPeriod(day, day).start();
        var inexpensive = book("10", 100, null); var expensive = book("1000", 1, null);
        List<User> buyers = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            var u = customer("Trùng tên"); buyers.add(u);
            create(u, OrderStatus.COMPLETED, time, inexpensive, i);
        }
        var bigSpender = customer("Mua giá trị cao");
        create(bigSpender, OrderStatus.COMPLETED, time, expensive, 1);
        create(bigSpender, OrderStatus.CANCELLED, time, expensive, 100);
        var filter = AnalyticsFilter.resolve("day", day, null, null, "day", null, null, null);
        var view = analytics.build(filter, "users", null);
        var moneyChart = view.charts().stream().filter(c -> c.title().contains("chi tiêu")).findFirst().orElseThrow();
        assertThat(moneyChart.points()).hasSize(5);
        assertThat(moneyChart.points().getFirst().url()).endsWith("/" + bigSpender.getId());
        assertThat(moneyChart.points().getFirst().value()).isEqualByComparingTo("1000");
        var quantityChart = view.charts().stream().filter(c -> c.title().contains("mua nhiều cuốn")).findFirst().orElseThrow();
        assertThat(quantityChart.points()).hasSize(5);
        assertThat(quantityChart.points().getFirst().url()).endsWith("/" + buyers.getLast().getId());
        var detail = analytics.build(filter, "users", bigSpender.getId());
        assertThat(detail.revenue()).isEqualByComparingTo("1000"); assertThat(detail.totalOrders()).isEqualTo(2);
    }

    @Test
    void validatesBadRangesAndReferenceFiltersUseWholeResultBeforePaging() {
        assertThatThrownBy(() -> AnalyticsFilter.resolve("custom", null, LocalDate.of(2024, 3, 2), LocalDate.of(2024, 3, 1), null, null, null, null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> AnalyticsFilter.resolve("custom", null, null, null, null, null, null, null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> AnalyticsFilter.resolve("invalid", null, null, null, null, null, null, null)).isInstanceOf(IllegalArgumentException.class);
        List<AnalyticsView.ReferenceStats> refs = new ArrayList<>();
        for (long i = 1; i <= 25; i++) refs.add(new AnalyticsView.ReferenceStats(i, "NXB " + i, "Hà Nội", true, i, i * 2, i, BigDecimal.valueOf(i * 100)));
        var filtered = ReferenceAnalyticsSearch.search(refs, "nxb", "hà nội", 2, "selling", "available", "active", "revenue", 0);
        assertThat(filtered.getTotalElements()).isEqualTo(24); assertThat(filtered.getContent()).hasSize(20);
        assertThat(filtered.getContent().getFirst().id()).isEqualTo(25);
        assertThat(ReferenceAnalyticsSearch.search(refs, null, null, 0, "unsold", "all", "all", "name", 0)).isEmpty();
    }

    private BigDecimal sum(AnalyticsView.Chart chart) { return chart.points().stream().map(AnalyticsView.Point::value).reduce(BigDecimal.ZERO, BigDecimal::add); }
    private User customer(String name) { return users.save(new User(name, UUID.randomUUID() + "@example.test", "encoded")); }
    private Book book(String price, int stock, Publisher publisher) {
        var token = UUID.randomUUID().toString();
        var category = categories.save(new Category("Thể loại " + token, token));
        var b = new Book("Sách " + token, token, new BigDecimal(price), stock, category); b.setPublisher(publisher);
        return books.saveAndFlush(b);
    }
    private CustomerOrder create(User user, OrderStatus status, Instant created, Book book, int quantity) {
        var order = new CustomerOrder("A-" + UUID.randomUUID().toString().substring(0, 15), user, "An", "0901234567", "Hà Nội");
        order.addItem(new OrderItem(order, book, quantity)); order.setStatus(status); orders.saveAndFlush(order);
        jdbc.update("update orders set created_at = ? where id = ?", Timestamp.from(created), order.getId());
        return order;
    }
}
