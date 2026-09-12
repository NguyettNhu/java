package com.yukihira.bookstore.admin.report;

import com.yukihira.bookstore.order.OrderStatus;
import java.math.BigDecimal;

public record OrderStatusTotal(OrderStatus status, Long count, BigDecimal amount) {}
