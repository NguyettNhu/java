package com.yukihira.bookstore.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    boolean existsByBookId(Long bookId);

    @Query("""
            select new com.yukihira.bookstore.admin.report.TopSellingBookView(
                item.book.id, item.bookTitle, sum(item.quantity), sum(item.subtotal))
            from OrderItem item
            where item.order.status = com.yukihira.bookstore.order.OrderStatus.COMPLETED
            group by item.book.id, item.bookTitle
            order by sum(item.quantity) desc, sum(item.subtotal) desc
            """)
    List<com.yukihira.bookstore.admin.report.TopSellingBookView> topSellingBooks(
            org.springframework.data.domain.Pageable pageable);
}
