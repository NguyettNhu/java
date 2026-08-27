package com.yukihira.bookstore.order;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class OrderTransitionPolicy {

    private static final Map<OrderStatus, List<OrderStatus>> TRANSITIONS = Map.of(
            OrderStatus.PENDING, List.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
            OrderStatus.CONFIRMED, List.of(OrderStatus.SHIPPING, OrderStatus.CANCELLED),
            OrderStatus.SHIPPING, List.of(OrderStatus.COMPLETED),
            OrderStatus.COMPLETED, List.of(),
            OrderStatus.CANCELLED, List.of()
    );

    public List<OrderStatus> allowedFrom(OrderStatus current) {
        return TRANSITIONS.getOrDefault(current, List.of());
    }

    public void validate(OrderStatus current, OrderStatus target) {
        if (!allowedFrom(current).contains(target)) {
            throw new OrderException("Không thể chuyển đơn hàng từ "
                    + OrderLabels.status(current) + " sang " + OrderLabels.status(target));
        }
    }
}
