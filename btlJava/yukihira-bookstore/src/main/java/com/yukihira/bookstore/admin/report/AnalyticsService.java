package com.yukihira.bookstore.admin.report;

import com.yukihira.bookstore.admin.catalog.ReferenceDataService;
import com.yukihira.bookstore.admin.catalog.ReferenceType;
import com.yukihira.bookstore.book.Book;
import com.yukihira.bookstore.order.OrderStatus;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import static com.yukihira.bookstore.admin.report.AnalyticsView.*;

@Service
public class AnalyticsService {
    private final EntityManager em;
    private final ReferenceDataService references;
    public AnalyticsService(EntityManager em, ReferenceDataService references) {
        this.em = em; this.references = references;
    }

    @Transactional(readOnly = true)
    public AnalyticsView build(AnalyticsFilter filter, String section, Long detailId) {
        // Fetch one catalog graph, not one query per book/author. No customer credentials are loaded.
        List<Book> catalog = em.createQuery("select distinct b from Book b join fetch b.category left join fetch b.publisher left join fetch b.authors", Book.class)
                .getResultList().stream().filter(b -> matches(b, filter))
                .filter(b -> !section.equals("books") || detailId == null || b.getId().equals(detailId)).toList();
        Map<Long, Book> byBook = catalog.stream().collect(Collectors.toMap(Book::getId, Function.identity()));
        boolean scoped = filter.publisherId() != null || filter.categoryId() != null || filter.authorId() != null
                || (section.equals("books") && detailId != null);
        // Scalar rows keep historical item subtotals intact, including when the current price changes.
        List<Object[]> orderRows = em.createQuery("select o.id, o.createdAt, o.status, o.totalAmount, o.user.id, o.user.fullName from CustomerOrder o "
                        + "where o.createdAt >= :start and o.createdAt < :end", Object[].class)
                .setParameter("start", filter.range().start()).setParameter("end", filter.range().endExclusive())
                .getResultList();
        // Detail restrictions are applied below, avoiding dynamically unbound query parameters.
        return summarize(filter, section, detailId, catalog, byBook, scoped, orderRows);
    }

    private AnalyticsView summarize(AnalyticsFilter filter, String section, Long detailId, List<Book> catalog,
                                    Map<Long, Book> byBook, boolean scoped, List<Object[]> orderRows) {
        var itemRows = em.createQuery("select i.order.id, i.book.id, sum(i.quantity), sum(i.subtotal) from OrderItem i "
                        + "where i.order.createdAt >= :start and i.order.createdAt < :end group by i.order.id, i.book.id", Object[].class)
                .setParameter("start", filter.range().start()).setParameter("end", filter.range().endExclusive()).getResultList();
        Map<Long, List<Object[]>> itemsByOrder = itemRows.stream().filter(r -> byBook.containsKey((Long) r[1]))
                .collect(Collectors.groupingBy(r -> (Long) r[0]));
        Map<OrderStatus, Long> statuses = new EnumMap<>(OrderStatus.class);
        Map<LocalDate, BigDecimal> timeline = new TreeMap<>();
        for (LocalDate d = filter.range().from(); !d.isAfter(filter.range().to()); d = d.plusDays(1)) timeline.putIfAbsent(filter.bucket(d), BigDecimal.ZERO);
        Map<Long, BigDecimal> customerMoney = new HashMap<>();
        Map<Long, Long> customerQuantity = new HashMap<>();
        Map<Long, String> customerNames = new HashMap<>();
        Map<Long, BigDecimal> bookMoney = new HashMap<>();
        Map<Long, Long> bookQuantity = new HashMap<>();
        BigDecimal revenue = BigDecimal.ZERO;
        long completed = 0;
        for (Object[] row : orderRows) {
            Long orderId = (Long) row[0], customerId = (Long) row[4];
            if (detailId != null && section.equals("users") && !customerId.equals(detailId)) continue;
            if (detailId != null && section.equals("orders") && !orderId.equals(detailId)) continue;
            List<Object[]> items = itemsByOrder.getOrDefault(orderId, List.of());
            if (scoped && items.isEmpty()) continue;
            OrderStatus status = (OrderStatus) row[2];
            statuses.merge(status, 1L, Long::sum);
            if (status != OrderStatus.COMPLETED) continue;
            BigDecimal amount = scoped ? items.stream().map(i -> (BigDecimal) i[3]).reduce(BigDecimal.ZERO, BigDecimal::add) : (BigDecimal) row[3];
            revenue = revenue.add(amount); completed++;
            LocalDate day = ((Instant) row[1]).atZone(ReportPeriod.ZONE).toLocalDate();
            timeline.merge(filter.bucket(day), amount, BigDecimal::add);
            customerMoney.merge(customerId, amount, BigDecimal::add);
            customerNames.put(customerId, (String) row[5]);
            for (Object[] item : items) {
                long quantity = ((Number) item[2]).longValue();
                bookQuantity.merge((Long) item[1], quantity, Long::sum);
                bookMoney.merge((Long) item[1], (BigDecimal) item[3], BigDecimal::add);
                customerQuantity.merge(customerId, quantity, Long::sum);
            }
        }
        List<Chart> charts = new ArrayList<>();
        String salesNote = "Đơn hoàn thành, theo ngày đặt hàng (giờ Việt Nam).";
        charts.add(new Chart("Doanh thu theo thời gian", "bar", "₫", salesNote + " Các kỳ ở biên chỉ cộng ngày nằm trong khoảng lọc.", timeline.entrySet().stream()
                .map(e -> new Point(bucketLabel(e.getKey(), filter.groupBy()), e.getValue(), null)).toList()));
        if (!section.equals("inventory")) {
            charts.add(new Chart("Phân bố trạng thái đơn", "pie", "đơn", "Đếm mỗi đơn một lần, kể cả đơn có nhiều sách.", Arrays.stream(OrderStatus.values())
                    .map(s -> new Point(s.getLabel(), BigDecimal.valueOf(statuses.getOrDefault(s, 0L)), null)).toList()));
        }
        if (Set.of("dashboard", "reports", "books", "categories", "inventory").contains(section)) {
            Map<Long, Long> categoryCounts = catalog.stream().collect(Collectors.groupingBy(b -> b.getCategory().getId(), Collectors.counting()));
            var categoryPoints = references.list(ReferenceType.CATEGORIES).stream().filter(c -> filter.categoryId() == null || c.id().equals(filter.categoryId()))
                    .map(c -> new Point(c.name(), BigDecimal.valueOf(categoryCounts.getOrDefault(c.id(), 0L)), "/admin/books?categoryId=" + c.id())).toList();
            charts.add(new Chart("Phân loại sách", "pie", "đầu sách", "Số đầu sách hiện tại theo thể loại, gồm cả sách ngừng bán.", categoryPoints));
        }
        if (Set.of("dashboard", "reports", "books", "users", "publishers", "categories", "authors").contains(section)) {
            charts.add(new Chart("Top 5 khách chi tiêu nhiều nhất", "bar", "₫", salesNote, ranked(customerMoney, id -> customerNames.get(id) + " (#" + id + ")", id -> "/admin/users/" + id, 5)));
            charts.add(new Chart("Top 5 khách mua nhiều cuốn nhất", "bar", "cuốn", salesNote, ranked(toMoney(customerQuantity), id -> customerNames.get(id) + " (#" + id + ")", id -> "/admin/users/" + id, 5)));
            charts.add(new Chart("Top 10 sách bán chạy", "bar", "cuốn", salesNote, ranked(toMoney(bookQuantity), id -> byBook.get(id).getTitle(), id -> "/admin/books/" + id, 10)));
        }
        long stock = catalog.stream().mapToLong(Book::getStock).sum();
        long low = catalog.stream().filter(b -> b.getStock() > 0 && b.getStock() <= 5).count();
        long empty = catalog.stream().filter(b -> b.getStock() == 0).count();
        if (Set.of("dashboard", "reports", "inventory", "books", "publishers", "categories", "authors").contains(section)) {
            charts.add(new Chart("Top 10 sách tồn kho nhiều nhất", "bar", "cuốn", "Tồn kho hiện tại, không phải tồn kho lịch sử trong kỳ.", catalog.stream()
                    .sorted(Comparator.comparingInt(Book::getStock).reversed().thenComparing(Book::getId)).limit(10)
                    .map(b -> new Point(b.getTitle(), BigDecimal.valueOf(b.getStock()), "/admin/books/" + b.getId())).toList()));
            charts.add(new Chart("Tình trạng tồn kho", "pie", "đầu sách", "Sắp hết: từ 1 đến 5 cuốn. Hết hàng: 0 cuốn.", List.of(
                    new Point("Còn trên 5 cuốn", BigDecimal.valueOf(catalog.size() - low - empty), null),
                    new Point("Sắp hết", BigDecimal.valueOf(low), null), new Point("Hết hàng", BigDecimal.valueOf(empty), null))));
        }
        ReferenceType referenceType = switch (section) { case "categories" -> ReferenceType.CATEGORIES; case "authors" -> ReferenceType.AUTHORS; default -> ReferenceType.PUBLISHERS; };
        List<ReferenceStats> refStats = referenceStats(referenceType, filter, catalog, bookMoney, bookQuantity);
        if (Set.of("reports", "publishers", "categories", "authors").contains(section)) {
            String noun = referenceType.getLabel().toLowerCase(Locale.ROOT);
            String attribution = referenceType == ReferenceType.AUTHORS ? " Sách nhiều tác giả được ghi nhận cho từng tác giả; không cộng các tác giả thành tổng doanh thu." : "";
            charts.add(new Chart("Doanh thu theo " + noun, "bar", "₫", salesNote + attribution, refStats.stream()
                    .sorted(Comparator.comparing(ReferenceStats::revenue).reversed().thenComparing(ReferenceStats::id)).limit(10)
                    .map(r -> new Point(r.name(), r.revenue(), null)).toList()));
            charts.add(new Chart("Số đầu sách theo " + noun, "bar", "đầu sách", "Danh mục hiện tại." + attribution, refStats.stream()
                    .sorted(Comparator.comparingLong(ReferenceStats::bookCount).reversed().thenComparing(ReferenceStats::id)).limit(10)
                    .map(r -> new Point(r.name(), BigDecimal.valueOf(r.bookCount()), null)).toList()));
        }
        long categoryTotal = filter.categoryId() == null && filter.publisherId() == null && filter.authorId() == null && detailId == null
                ? references.list(ReferenceType.CATEGORIES).size() : catalog.stream().map(b -> b.getCategory().getId()).distinct().count();
        return new AnalyticsView(filter, catalog.size(), categoryTotal, stock, low, empty,
                catalog.stream().map(b -> b.getPrice().multiply(BigDecimal.valueOf(b.getStock()))).reduce(BigDecimal.ZERO, BigDecimal::add),
                bookQuantity.values().stream().mapToLong(Long::longValue).sum(), statuses.values().stream().mapToLong(Long::longValue).sum(),
                completed, revenue, customerMoney.size(), charts, refStats);
    }

    private boolean matches(Book b, AnalyticsFilter f) {
        return (f.publisherId() == null || b.getPublisher() != null && b.getPublisher().getId().equals(f.publisherId()))
                && (f.categoryId() == null || b.getCategory().getId().equals(f.categoryId()))
                && (f.authorId() == null || b.getAuthors().stream().anyMatch(a -> a.getId().equals(f.authorId())));
    }

    private List<ReferenceStats> referenceStats(ReferenceType type, AnalyticsFilter f, List<Book> catalog, Map<Long, BigDecimal> money, Map<Long, Long> quantities) {
        Long selection = switch (type) { case CATEGORIES -> f.categoryId(); case AUTHORS -> f.authorId(); case PUBLISHERS -> f.publisherId(); };
        return references.list(type).stream().filter(r -> selection == null || selection.equals(r.id())).map(r -> {
            var matching = catalog.stream().filter(b -> switch (type) {
                case CATEGORIES -> b.getCategory().getId().equals(r.id());
                case PUBLISHERS -> b.getPublisher() != null && b.getPublisher().getId().equals(r.id());
                case AUTHORS -> b.getAuthors().stream().anyMatch(a -> a.getId().equals(r.id()));
            }).toList();
            return new ReferenceStats(r.id(), r.name(), r.details(), r.active(), matching.size(), matching.stream().mapToLong(Book::getStock).sum(),
                    matching.stream().mapToLong(b -> quantities.getOrDefault(b.getId(), 0L)).sum(),
                    matching.stream().map(b -> money.getOrDefault(b.getId(), BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add));
        }).toList();
    }
    private Map<Long, BigDecimal> toMoney(Map<Long, Long> values) {
        return values.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> BigDecimal.valueOf(e.getValue())));
    }
    private List<Point> ranked(Map<Long, BigDecimal> values, Function<Long, String> name, Function<Long, String> url, int limit) {
        return values.entrySet().stream().sorted(Map.Entry.<Long, BigDecimal>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
                .limit(limit).map(e -> new Point(name.apply(e.getKey()), e.getValue(), url.apply(e.getKey()))).toList();
    }
    private String bucketLabel(LocalDate day, String group) {
        return switch (group) {
            case "week" -> "Tuần " + day.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            case "month" -> day.format(DateTimeFormatter.ofPattern("MM/yyyy"));
            case "quarter" -> "Quý " + ((day.getMonthValue() - 1) / 3 + 1) + "/" + day.getYear();
            case "year" -> Integer.toString(day.getYear());
            default -> day.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        };
    }
}
