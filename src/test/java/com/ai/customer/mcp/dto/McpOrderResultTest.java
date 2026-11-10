
package com.ai.customer.mcp.dto;

import com.ai.customer.dto.OrderResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class McpOrderResultTest {

    @Test
    void shouldReturnStructuredUnavailableError() {

        McpOrderResult result = McpOrderResult.unavailable();

        assertFalse(result.success());
        assertNull(result.order());

        assertNotNull(result.error());
        assertEquals(
                "ORDER_UNAVAILABLE",
                result.error().code()
        );
        assertEquals(
                "Order unavailable",
                result.error().message()
        );
        assertFalse(result.error().retryable());
    }

    @Test
    void shouldReturnSuccessfulOrderResult() {

        OrderResponse order = new OrderResponse(
                "ORD-1001",
                "SHIPPED",
                java.time.LocalDate.of(2026, 11, 10)
        );

        McpOrderResult result = McpOrderResult.success(order);

        assertTrue(result.success());
        assertEquals(order, result.order());
        assertNull(result.error());
    }
}
