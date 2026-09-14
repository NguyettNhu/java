package com.yukihira.bookstore.order;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long>, JpaSpecificationExecutor<CustomerOrder> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from CustomerOrder o where o.id = :id")
    Optional<CustomerOrder> findForUpdate(@Param("id") Long id);

        // Tổng hợp số đơn và giá trị đơn theo từng trạng thái trong khoảng thời gian.
    @Query("select new com.yukihira.bookstore.admin.report.OrderStatusTotal(o.status, count(o), sum(o.totalAmount)) "
            + "from CustomerOrder o where o.createdAt >= :from and o.createdAt < :to group by o.status")
    List<com.yukihira.bookstore.admin.report.OrderStatusTotal> reportTotals(
            @Param("from") java.time.Instant from, @Param("to") java.time.Instant to);
    @EntityGraph(attributePaths = {"items", "items.book"})
    @Query("select customerOrder from CustomerOrder customerOrder where customerOrder.id = :id")
    Optional<CustomerOrder> findDetailedById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"items", "items.book"})
    @Query("select customerOrder from CustomerOrder customerOrder where customerOrder.id = :id and lower(customerOrder.user.email) = lower(:email)")
    Optional<CustomerOrder> findOwnedDetailedById(@Param("id") Long id, @Param("email") String email);

    Optional<CustomerOrder> findByOrderCode(String orderCode);

    @EntityGraph(attributePaths = {"items"})
    List<CustomerOrder> findByUserEmailIgnoreCaseOrderByCreatedAtDesc(String email);

    long countByStatus(OrderStatus status);

        // Chỉ cộng doanh thu của các đơn đã hoàn thành.
    @Query("select coalesce(sum(customerOrder.totalAmount), 0) from CustomerOrder customerOrder "
            + "where customerOrder.status = com.yukihira.bookstore.order.OrderStatus.COMPLETED")
    BigDecimal completedRevenue();
}
