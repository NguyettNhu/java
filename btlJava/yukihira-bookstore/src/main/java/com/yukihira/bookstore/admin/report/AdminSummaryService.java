package com.yukihira.bookstore.admin.report;

import com.yukihira.bookstore.admin.report.AnalyticsView.Chart;
import com.yukihira.bookstore.admin.report.AnalyticsView.Point;
import com.yukihira.bookstore.book.Book;
import com.yukihira.bookstore.book.BookSearchQuery;
import com.yukihira.bookstore.book.BookSpecifications;
import com.yukihira.bookstore.book.BookStatus;
import com.yukihira.bookstore.category.Category;
import com.yukihira.bookstore.order.CustomerOrder;
import com.yukihira.bookstore.order.OrderSearchQuery;
import com.yukihira.bookstore.order.OrderSpecifications;
import com.yukihira.bookstore.order.OrderStatus;
import com.yukihira.bookstore.order.OrderSummaryView;
import com.yukihira.bookstore.user.User;
import com.yukihira.bookstore.user.UserSearchQuery;
import com.yukihira.bookstore.user.UserSpecifications;
import com.yukihira.bookstore.user.UserStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Số liệu tóm tắt riêng của từng trang quản lý. Mỗi trang chỉ tổng hợp đúng phạm vi bảng
 * bên dưới đang lọc, bằng một hoặc hai truy vấn gom nhóm, thay cho khối phân tích dùng chung.
 */
@Service
public class AdminSummaryService {
    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Set<OrderStatus> OPEN = Set.of(OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.SHIPPING);

    private final EntityManager em;

    public AdminSummaryService(EntityManager em) {
        this.em = em;
    }

    /**
     * Tổng hợp sách của trang quản lý sách và trang tồn kho trong một truy vấn gom theo
     * (trạng thái, số tồn). Thẻ số liệu tính đúng bộ lọc của bảng; số trên lối tắt trạng thái
     * bỏ qua bộ lọc trạng thái, số trên lối tắt mức tồn bỏ qua bộ lọc mức tồn, nên bấm vào
     * lối tắt nào cũng ra đúng số dòng đã hiện.
     */
    @Transactional(readOnly = true)
    public BookSummary books(BookSearchQuery query, BookStatus status, String stockLevel, boolean withCharts) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<Book> book = cq.from(Book.class);
        Expression<BigDecimal> price = book.get("price");
        cq.select(cb.array(book.get("status"), book.get("stock"), cb.count(book), cb.sum(price), cb.min(price), cb.max(price)))
                .where(matching(cb, book, Book.class, BookSpecifications.from(query, false)))
                .groupBy(book.get("status"), book.get("stock"));
        List<StockCell> cells = em.createQuery(cq).getResultList().stream()
                .map(row -> new StockCell((BookStatus) row[0], ((Number) row[1]).intValue(), number(row[2]),
                        money(row[3]), money(row[4]), money(row[5]))).toList();
        List<StockCell> inTable = cells.stream().filter(c -> c.is(status) && c.at(stockLevel)).toList();
        Map<BookStatus, Long> statuses = new EnumMap<>(BookStatus.class);
        cells.stream().filter(c -> c.at(stockLevel)).forEach(c -> statuses.merge(c.status(), c.count(), Long::sum));
        List<StockCell> levels = cells.stream().filter(c -> c.is(status)).toList();
        long levelTotal = sum(levels, c -> true), low = sum(levels, c -> c.at("low")), empty = sum(levels, c -> c.at("empty"));
        List<Chart> charts = new ArrayList<>();
        if (withCharts) {
            charts.add(stockByCategory(cb, BookSpecifications.admin(query, status, stockLevel)));
            long plenty = sum(levels, c -> c.stock() > 5);
            List<Point> points = new ArrayList<>(List.of(
                    new Point("Còn trên 5 cuốn", BigDecimal.valueOf(plenty), null),
                    new Point("Sắp hết (1–5 cuốn)", BigDecimal.valueOf(low), null),
                    new Point("Hết hàng", BigDecimal.valueOf(empty), null)));
            long other = levelTotal - plenty - low - empty;
            if (other > 0) points.add(new Point("Ngừng bán, còn 1–5 cuốn", BigDecimal.valueOf(other), null));
            charts.add(new Chart("stock-levels", "Mức tồn kho", "pie", "đầu sách",
                    "Trong phạm vi tìm kiếm, không phụ thuộc lối tắt mức tồn đang chọn. Sắp hết chỉ tính sách chưa ngừng bán.", points));
        }
        return new BookSummary(sum(inTable, c -> true),
                inTable.stream().mapToLong(c -> (long) c.stock() * c.count()).sum(),
                inTable.stream().map(c -> c.priceTotal().multiply(BigDecimal.valueOf(c.stock()))).reduce(BigDecimal.ZERO, BigDecimal::add),
                inTable.stream().map(StockCell::minPrice).min(BigDecimal::compareTo).orElse(null),
                inTable.stream().map(StockCell::maxPrice).max(BigDecimal::compareTo).orElse(null),
                inTable.stream().map(StockCell::priceTotal).reduce(BigDecimal.ZERO, BigDecimal::add),
                statuses, levelTotal, low, empty, charts);
    }

    private static long sum(List<StockCell> cells, java.util.function.Predicate<StockCell> condition) {
        return cells.stream().filter(condition).mapToLong(StockCell::count).sum();
    }

    /** Một nhóm sách cùng trạng thái và cùng số tồn. */
    private record StockCell(BookStatus status, int stock, long count, BigDecimal priceTotal, BigDecimal minPrice, BigDecimal maxPrice) {
        boolean is(BookStatus wanted) { return wanted == null || status == wanted; }
        /** Cùng quy tắc với bộ lọc mức tồn của bảng: “sắp hết” bỏ qua sách đã ngừng bán. */
        boolean at(String level) {
            return switch (level == null ? "" : level) {
                case "low" -> stock >= 1 && stock <= 5 && status != BookStatus.INACTIVE;
                case "empty" -> stock == 0;
                default -> true;
            };
        }
    }

    private Chart stockByCategory(CriteriaBuilder cb, Specification<Book> spec) {
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<Book> book = cq.from(Book.class);
        Join<Book, Category> category = book.join("category");
        Expression<Integer> stock = cb.sum(book.<Integer>get("stock"));
        cq.select(cb.array(category.get("id"), category.get("name"), stock))
                .where(matching(cb, book, Book.class, spec))
                .groupBy(category.get("id"), category.get("name"))
                .orderBy(cb.desc(stock), cb.asc(category.get("name")));
        List<Point> points = em.createQuery(cq).setMaxResults(10).getResultList().stream()
                .map(row -> new Point((String) row[1], BigDecimal.valueOf(number(row[2])),
                        "/admin/books?sort=stock-desc&categoryId=" + row[0])).toList();
        return new Chart("stock-by-category", "Tồn kho theo thể loại", "bar", "cuốn",
                "10 thể loại có nhiều sách trong kho nhất, trong đúng phạm vi bảng bên dưới.", points);
    }

    /** Đếm đơn và cộng tiền theo trạng thái, trong đúng từ khóa và khoảng ngày của trang đơn hàng. */
    @Transactional(readOnly = true)
    public OrderSummary orders(OrderSearchQuery query) {
        Specification<CustomerOrder> spec = OrderSpecifications.from(new OrderSearchQuery(query.keyword(), null, query.from(), query.to()));
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<CustomerOrder> order = cq.from(CustomerOrder.class);
        cq.select(cb.array(order.get("status"), cb.count(order), cb.sum(order.<BigDecimal>get("totalAmount"))))
                .where(spec.toPredicate(order, cq, cb)).groupBy(order.get("status"));
        Map<OrderStatus, OrderStatusTotal> totals = new EnumMap<>(OrderStatus.class);
        for (Object[] row : em.createQuery(cq).getResultList())
            totals.put((OrderStatus) row[0], new OrderStatusTotal((OrderStatus) row[0], number(row[1]), money(row[2])));
        return new OrderSummary(Arrays.stream(OrderStatus.values())
                .map(s -> totals.getOrDefault(s, new OrderStatusTotal(s, 0L, BigDecimal.ZERO))).toList());
    }

    /**
     * Khách hàng khớp bộ lọc của trang: số khách, khách mới và chi tiêu. Số đếm theo trạng thái
     * tài khoản bỏ qua bộ lọc trạng thái để làm lối tắt lọc.
     */
    @Transactional(readOnly = true)
    public CustomerListSummary customers(String keyword, UserStatus status) {
        Specification<User> spec = UserSpecifications.customers(new UserSearchQuery(keyword, null));
        LocalDate since = LocalDate.now(ReportPeriod.ZONE).minusDays(30);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<User> user = cq.from(User.class);
        cq.select(cb.array(user.get("status"), cb.count(user),
                        countWhere(cb, cb.greaterThanOrEqualTo(user.<Instant>get("createdAt"), since.atStartOfDay(ReportPeriod.ZONE).toInstant()))))
                .where(spec.toPredicate(user, cq, cb)).groupBy(user.get("status"));
        Map<UserStatus, Long> statuses = new EnumMap<>(UserStatus.class);
        long newcomers = 0;
        for (Object[] row : em.createQuery(cq).getResultList()) {
            statuses.put((UserStatus) row[0], number(row[1]));
            if (status == null || status == row[0]) newcomers += number(row[2]);
        }
        // Chi tiêu chỉ cộng đơn hoàn thành, xếp sẵn từ cao xuống thấp để lấy top 5.
        CriteriaQuery<Object[]> bq = cb.createQuery(Object[].class);
        Root<CustomerOrder> order = bq.from(CustomerOrder.class);
        Join<CustomerOrder, User> buyer = order.join("user");
        Expression<BigDecimal> spent = cb.sum(order.<BigDecimal>get("totalAmount"));
        bq.select(cb.array(buyer.get("id"), buyer.get("fullName"), spent, cb.count(order)))
                .where(cb.equal(order.get("status"), OrderStatus.COMPLETED), matching(cb, buyer, User.class, UserSpecifications.customers(new UserSearchQuery(keyword, status))))
                .groupBy(buyer.get("id"), buyer.get("fullName")).orderBy(cb.desc(spent), cb.asc(buyer.get("id")));
        List<Object[]> buyers = em.createQuery(bq).getResultList();
        BigDecimal total = buyers.stream().map(row -> money(row[2])).reduce(BigDecimal.ZERO, BigDecimal::add);
        long completed = buyers.stream().mapToLong(row -> number(row[3])).sum();
        Chart top = new Chart("top-spenders", "Top 5 khách chi tiêu nhiều nhất", "bar", "₫",
                "Tổng tiền các đơn hoàn thành từ trước đến nay của các khách trong bảng bên dưới.", buyers.stream().limit(5)
                .map(row -> new Point(row[1] + " (#" + row[0] + ")", money(row[2]), "/admin/users/" + row[0])).toList());
        long shown = status == null ? statuses.values().stream().mapToLong(Long::longValue).sum() : statuses.getOrDefault(status, 0L);
        return new CustomerListSummary(statuses, shown, newcomers, since, buyers.size(), total, completed, top);
    }

    /** Doanh số từ trước đến nay của một cuốn sách, gom theo trạng thái đơn trong một truy vấn. */
    @Transactional(readOnly = true)
    public BookSales bookSales(Long bookId) {
        List<Object[]> rows = em.createQuery("select o.status, count(distinct o.id), sum(i.quantity), sum(i.subtotal), "
                        + "count(distinct o.user.id), max(o.createdAt) from OrderItem i join i.order o "
                        + "where i.book.id = :book group by o.status", Object[].class)
                .setParameter("book", bookId).getResultList();
        long sold = 0, completed = 0, buyers = 0, waitingCopies = 0, waitingOrders = 0, cancelled = 0;
        BigDecimal revenue = BigDecimal.ZERO;
        Instant last = null;
        for (Object[] row : rows) {
            OrderStatus status = (OrderStatus) row[0];
            Instant latest = (Instant) row[5];
            if (last == null || latest.isAfter(last)) last = latest;
            if (status == OrderStatus.COMPLETED) {
                completed = number(row[1]); sold = number(row[2]); revenue = money(row[3]); buyers = number(row[4]);
            } else if (status == OrderStatus.CANCELLED) {
                cancelled = number(row[1]);
            } else {
                waitingOrders += number(row[1]); waitingCopies += number(row[2]);
            }
        }
        return new BookSales(sold, revenue, completed, buyers, waitingCopies, waitingOrders, cancelled, last);
    }

    private static Expression<Long> countWhere(CriteriaBuilder cb, Predicate condition) {
        return cb.sum(cb.<Long>selectCase().when(condition, 1L).otherwise(0L));
    }

    /**
     * Lọc qua truy vấn con theo khóa chính: bộ lọc sách có thể nối bảng tác giả và nhân bản
     * dòng, nên áp thẳng vào truy vấn gom nhóm sẽ cộng trùng tồn kho và tiền.
     */
    private static <T> Predicate matching(CriteriaBuilder cb, jakarta.persistence.criteria.Path<T> target,
                                          Class<T> type, Specification<T> spec) {
        // Truy vấn nháp chỉ để hứng distinct() của bộ lọc từ khóa, tránh đổi câu truy vấn gom nhóm bên ngoài.
        CriteriaQuery<Object> scratch = cb.createQuery();
        Subquery<Long> ids = scratch.subquery(Long.class);
        Root<T> candidate = ids.from(type);
        ids.select(candidate.<Long>get("id")).where(spec.toPredicate(candidate, scratch, cb));
        return target.get("id").in(ids);
    }

    private static long number(Object value) {
        return value == null ? 0 : ((Number) value).longValue();
    }

    private static BigDecimal money(Object value) {
        if (value == null) return BigDecimal.ZERO;
        return value instanceof BigDecimal decimal ? decimal : new BigDecimal(value.toString());
    }

    private static BigDecimal average(BigDecimal total, long count) {
        return count == 0 ? BigDecimal.ZERO : total.divide(BigDecimal.valueOf(count), 0, RoundingMode.HALF_UP);
    }

    private static String day(Instant instant) {
        return instant == null ? null : DAY.format(instant.atZone(ReportPeriod.ZONE));
    }

    /**
     * Thẻ số liệu (tổng, tồn, giá trị, giá) theo đúng bộ lọc của bảng; {@code statuses} và
     * {@code levelTotal/lowStock/emptyStock} là số đếm cho các lối tắt lọc.
     */
    public record BookSummary(long total, long stock, BigDecimal stockValue, BigDecimal minPrice, BigDecimal maxPrice,
                              BigDecimal priceTotal, Map<BookStatus, Long> statuses, long levelTotal,
                              long lowStock, long emptyStock, List<Chart> charts) {
        public long count(BookStatus status) { return statuses.getOrDefault(status, 0L); }
        public long statusTotal() { return statuses.values().stream().mapToLong(Long::longValue).sum(); }
        public BigDecimal averagePrice() { return average(priceTotal, total); }
        public Chart chart(String key) { return charts.stream().filter(c -> c.key().equals(key)).findFirst().orElse(null); }
    }

    public record OrderSummary(List<OrderStatusTotal> statuses) {
        public long total() { return statuses.stream().mapToLong(OrderStatusTotal::count).sum(); }
        public long count(OrderStatus status) { return row(status).count(); }
        public BigDecimal revenue() { return row(OrderStatus.COMPLETED).amount(); }
        public long open() { return statuses.stream().filter(s -> OPEN.contains(s.status())).mapToLong(OrderStatusTotal::count).sum(); }
        public BigDecimal openAmount() {
            return statuses.stream().filter(s -> OPEN.contains(s.status())).map(OrderStatusTotal::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        public BigDecimal averageCompleted() { return average(revenue(), count(OrderStatus.COMPLETED)); }
        /** Phần trăm đơn bị hủy trên tổng số đơn, làm tròn một chữ số. */
        public BigDecimal cancelledShare() {
            long total = total();
            return total == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(count(OrderStatus.CANCELLED) * 100)
                    .divide(BigDecimal.valueOf(total), 1, RoundingMode.HALF_UP);
        }
        private OrderStatusTotal row(OrderStatus status) {
            return statuses.stream().filter(s -> s.status() == status).findFirst().orElseThrow();
        }
    }

    public record CustomerListSummary(Map<UserStatus, Long> statuses, long total, long newCustomers, LocalDate newSince,
                                      long buyers, BigDecimal spent, long completedOrders, Chart topSpenders) {
        public long statusTotal() { return statuses.values().stream().mapToLong(Long::longValue).sum(); }
        public long count(UserStatus status) { return statuses.getOrDefault(status, 0L); }
        public BigDecimal averageSpent() { return average(spent, buyers); }
        public String newSinceLabel() { return DAY.format(newSince); }
        public List<Chart> charts() { return List.of(topSpenders); }
    }

    public record BookSales(long soldCopies, BigDecimal revenue, long completedOrders, long buyers,
                            long waitingCopies, long waitingOrders, long cancelledOrders, Instant lastOrderAt) {
        public String lastOrderLabel() { return day(lastOrderAt); }
    }

    /** Tóm tắt lịch sử mua của một khách, tính từ danh sách đơn trang hồ sơ đã tải sẵn. */
    public record CustomerSummary(long totalOrders, long completedOrders, BigDecimal spent, long openOrders,
                                  long cancelledOrders, long books, Instant lastOrderAt) {
        public static CustomerSummary of(List<OrderSummaryView> orders) {
            var completed = orders.stream().filter(o -> o.status() == OrderStatus.COMPLETED).toList();
            return new CustomerSummary(orders.size(), completed.size(),
                    completed.stream().map(OrderSummaryView::totalAmount).reduce(BigDecimal.ZERO, BigDecimal::add),
                    orders.stream().filter(o -> OPEN.contains(o.status())).count(),
                    orders.stream().filter(o -> o.status() == OrderStatus.CANCELLED).count(),
                    completed.stream().mapToLong(OrderSummaryView::itemCount).sum(),
                    orders.stream().map(OrderSummaryView::createdAt).max(Instant::compareTo).orElse(null));
        }
        public BigDecimal averageCompleted() { return average(spent, completedOrders); }
        public String lastOrderLabel() { return day(lastOrderAt); }
    }
}
