package com.ai.customer.repository;

import com.ai.customer.entity.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerOrderRepository
        extends JpaRepository<CustomerOrder, String> {

    Optional<CustomerOrder> findByOrderIdAndCustomerId(
            String orderId,
            String customerId);
}