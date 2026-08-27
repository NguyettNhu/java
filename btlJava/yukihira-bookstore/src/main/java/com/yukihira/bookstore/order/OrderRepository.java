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

    @Query("select coalesce(sum(customerOrder.totalAmount), 0) from CustomerOrder customerOrder "
            + "where customerOrder.status = com.yukihira.bookstore.order.OrderStatus.COMPLETED")
    BigDecimal completedRevenue();
}
