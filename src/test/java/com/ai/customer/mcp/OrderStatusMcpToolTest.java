
package com.ai.customer.mcp;

import com.ai.customer.exception.OrderNotFoundException;
import com.ai.customer.service.CustomerIdentityService;
import com.ai.customer.service.OrderService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderStatusMcpToolTest {

    private final CustomerIdentityService identityService =
            mock(CustomerIdentityService.class);

    private final OrderService orderService =
            mock(OrderService.class);

    private final OrderStatusMcpTool tool =
            new OrderStatusMcpTool(identityService, orderService);

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    private void authenticate() {
        Jwt jwt = new Jwt(
                "test-token",
                Instant.now(),
                Instant.now().plusSeconds(300),
                java.util.Map.of("alg", "RS256"),
                java.util.Map.of(
                        "sub", "keycloak-subject-101",
                        "iss", "http://localhost:8180/realms/customer-ai"
                )
        );

        var authentication = new TestingAuthenticationToken(
                jwt,
                null,
                List.of(
                        new org.springframework.security.core.authority
                                .SimpleGrantedAuthority("SCOPE_orders:read")
                )
        );

        authentication.setAuthenticated(true);

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }

    @Test
    void rejectsOrderNotOwnedByAuthenticatedCustomer() {
        authenticate();

        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        when(identityService.resolveCustomerId(jwt))
                .thenReturn("customer-101");

        when(orderService.getOrder("ORD-2002", "customer-101"))
                .thenThrow(new OrderNotFoundException("Order Not found"));

        assertThrows(
                OrderNotFoundException.class,
                () -> tool.getOrderStatus("ORD-2002")
        );

        verify(orderService).getOrder(
                "ORD-2002",
                "customer-101"
        );
    }
}
