package com.ai.customer.mcp;

import com.ai.customer.dto.OrderResponse;
import com.ai.customer.entity.CustomerOrder;
import com.ai.customer.exception.OrderNotFoundException;
import com.ai.customer.service.CustomerIdentityService;
import com.ai.customer.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderStatusMcpTool {

    private final CustomerIdentityService identityService;
    private final OrderService orderService;

    public OrderStatusMcpTool(
            CustomerIdentityService identityService,
            OrderService orderService) {
        this.identityService = identityService;
        this.orderService = orderService;
    }

    @Tool(
            name = "get_order_status",
            description = "Retrieve an order's status and estimated delivery date"
    )
    public OrderResponse getOrderStatus(String orderId) {

        log.info("MCP get_order_status invoked");

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof Jwt jwt)) {

            throw new AccessDeniedException(
                    "Authenticated JWT required"
            );
        }

        if (authentication.getAuthorities().stream()
                .noneMatch(authority ->
                        authority.getAuthority().equals("SCOPE_orders:read"))) {

            throw new AccessDeniedException(
                    "Required permission missing"
            );
        }

        String customerId = identityService.resolveCustomerId(jwt);
        try {
            CustomerOrder order = orderService.getOrder(
                    orderId,
                    customerId
            );

            return OrderResponse.from(order);

        } catch (OrderNotFoundException ex) {
            log.info(
                    "MCP order lookup rejected: order not found or not owned"
            );
            throw ex;
        }
    }
}
