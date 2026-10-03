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

        // Thời điểm đặt và giá trị của các đơn hoàn thành kể từ một mốc, dùng cho biểu đồ doanh thu gần đây.
    @Query("select o.createdAt, o.totalAmount from CustomerOrder o "
            + "where o.status = com.yukihira.bookstore.order.OrderStatus.COMPLETED and o.createdAt >= :from")
    List<Object[]> completedSince(@Param("from") java.time.Instant from);
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
