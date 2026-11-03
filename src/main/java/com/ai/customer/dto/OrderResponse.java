package com.ai.customer.dto;

import com.ai.customer.entity.CustomerOrder;

import java.time.LocalDate;

public record OrderResponse(
        String orderId,
        String status,
        LocalDate estimatedDelivery
) {
    public static OrderResponse from(CustomerOrder order) {
        return new OrderResponse(
                order.getOrderId(),
                order.getStatus().name(),
                order.getEstimatedDelivery()
        );
    }
}
