package com.ai.customer.service;

import com.ai.customer.entity.CustomerOrder;
import com.ai.customer.exception.OrderNotFoundException;
import com.ai.customer.repository.CustomerOrderRepository;
import org.springframework.stereotype.Service;

@Service

public class OrderService {

    private final CustomerOrderRepository customerOrderRepository;

    public OrderService(CustomerOrderRepository customerOrderRepository) {
        this.customerOrderRepository = customerOrderRepository;
    }
    public CustomerOrder getOrder(
            String orderId,
            String authenticatedCustomerId) {

        return customerOrderRepository.findByOrderIdAndCustomerId(orderId, authenticatedCustomerId)
                .orElseThrow(() -> new OrderNotFoundException("Order Not found"));

    }
}
