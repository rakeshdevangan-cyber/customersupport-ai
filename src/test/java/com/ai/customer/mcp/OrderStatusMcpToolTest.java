
package com.ai.customer.mcp;

import com.ai.customer.entity.CustomerOrder;
import com.ai.customer.entity.OrderStatus;
import com.ai.customer.exception.OrderNotFoundException;
import com.ai.customer.mcp.dto.McpOrderResult;
import com.ai.customer.service.CustomerIdentityService;
import com.ai.customer.service.OrderService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.time.LocalDate;
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

        McpOrderResult result = tool.getOrderStatus("ORD-2002");

        assertFalse(result.success());
        assertNull(result.order());

        assertNotNull(result.error());
        assertEquals("ORDER_UNAVAILABLE", result.error().code());
        assertEquals("Order unavailable", result.error().message());
        assertFalse(result.error().retryable());
        verify(orderService).getOrder("ORD-2002", "customer-101");
    }


    @Test
    void returnsOrderOwnedByAuthenticatedCustomer() {

        // Arrange: Mock an authenticated customer
        Jwt jwt = mock(Jwt.class);

        Authentication authentication = mock(Authentication.class);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(authentication.getAuthorities()).thenAnswer(invocation ->
                List.of(
                        new SimpleGrantedAuthority("SCOPE_orders:read")
                )
        );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        // Resolve the trusted customer identity
        when(identityService.resolveCustomerId(jwt))
                .thenReturn("customer-101");

        // Mock an order owned by this customer
        CustomerOrder order = new CustomerOrder();
        order.setOrderId("ORD-1001");
        order.setCustomerId("customer-101");
        order.setStatus(OrderStatus.SHIPPED);
        order.setEstimatedDelivery(
                LocalDate.of(2026, 11, 10)
        );

        when(orderService.getOrder("ORD-1001", "customer-101"))
                .thenReturn(order);

        // Act
        McpOrderResult result = tool.getOrderStatus("ORD-1001");

        // Assert: Structured success response
        assertTrue(result.success());
        assertNotNull(result.order());
        assertNull(result.error());

        assertEquals(
                "ORD-1001",
                result.order().orderId()
        );

        assertEquals(
                "SHIPPED",
                result.order().status()
        );

        assertEquals(
                LocalDate.of(2026, 11, 10),
                result.order().estimatedDelivery()
        );

        // Verify ownership enforcement
        verify(identityService).resolveCustomerId(jwt);

        verify(orderService).getOrder(
                "ORD-1001",
                "customer-101"
        );
    }

}
