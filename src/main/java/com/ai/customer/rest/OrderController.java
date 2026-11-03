package com.ai.customer.rest;

import com.ai.customer.dto.OrderResponse;
import com.ai.customer.entity.CustomerOrder;
import com.ai.customer.service.CustomerIdentityService;
import com.ai.customer.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    private final CustomerIdentityService customerIdentityService;

    public OrderController(OrderService orderService,
                           CustomerIdentityService customerIdentityService) {
        this.orderService = orderService;
        this.customerIdentityService = customerIdentityService;
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrder(
            @PathVariable String orderId,
            @AuthenticationPrincipal Jwt jwt)  {

        log.info("=======Request received from python===");
        if (jwt == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required"
            );
        }


        String customerId = customerIdentityService.resolveCustomerId(jwt);

        log.info("Requested Order ID: [" + orderId + "]");
        log.info("Resolved Customer ID: [" + customerId + "]");

        CustomerOrder order = orderService.getOrder(
                orderId,
                customerId
        );

        System.out.println("Order retrieved: " + order.getOrderId());

        return OrderResponse.from(order);
    }
}