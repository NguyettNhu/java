package com.yukihira.bookstore.order;

import com.yukihira.bookstore.common.domain.BaseEntity;
import com.yukihira.bookstore.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders",
        uniqueConstraints = @UniqueConstraint(name = "uk_orders_code", columnNames = "order_code"),
        indexes = {
                @Index(name = "idx_orders_user", columnList = "user_id"),
                @Index(name = "idx_orders_status", columnList = "status"),
                @Index(name = "idx_orders_created_at", columnList = "created_at")
        })
public class CustomerOrder extends BaseEntity {

    @Column(name = "order_code", nullable = false, length = 30)
    private String orderCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "receiver_name", nullable = false, length = 100)
    private String receiverName;

    @Column(name = "receiver_phone", nullable = false, length = 20)
    private String receiverPhone;

    @Column(name = "shipping_address", nullable = false, length = 255)
    private String shippingAddress;

    @Column(length = 500)
    private String note;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 30)
    private PaymentMethod paymentMethod = PaymentMethod.COD;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "stock_restored", nullable = false)
    private boolean stockRestored;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    protected CustomerOrder() {
    }

    public CustomerOrder(String orderCode, User user, String receiverName,
                         String receiverPhone, String shippingAddress) {
        this.orderCode = orderCode;
        this.user = user;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.shippingAddress = shippingAddress;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        totalAmount = totalAmount.add(item.getSubtotal());
    }

    public String getOrderCode() { return orderCode; }
    public User getUser() { return user; }
    public String getReceiverName() { return receiverName; }
    public String getReceiverPhone() { return receiverPhone; }
    public String getShippingAddress() { return shippingAddress; }
    public String getNote() { return note; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public OrderStatus getStatus() { return status; }
    public boolean isStockRestored() { return stockRestored; }
    public List<OrderItem> getItems() { return items; }
    public void setNote(String note) { this.note = note; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setStockRestored(boolean stockRestored) { this.stockRestored = stockRestored; }
}
