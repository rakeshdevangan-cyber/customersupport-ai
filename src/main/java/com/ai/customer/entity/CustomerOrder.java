package com.ai.customer.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "customer_orders")
public class CustomerOrder {

    @Id
    private String orderId;

    @Column(nullable = false)
    private String customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private LocalDate estimatedDelivery;

    protected CustomerOrder() {
    }

    public CustomerOrder(
            String orderId,
            String customerId,
            OrderStatus status,
            LocalDate estimatedDelivery) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.status = status;
        this.estimatedDelivery = estimatedDelivery;
    }

    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public OrderStatus getStatus() { return status; }
    public LocalDate getEstimatedDelivery() { return estimatedDelivery; }
}
