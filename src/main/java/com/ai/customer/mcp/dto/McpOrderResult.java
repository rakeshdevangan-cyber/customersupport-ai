package com.ai.customer.mcp.dto;

import com.ai.customer.dto.OrderResponse;

public record McpOrderResult(
        boolean success,
        OrderResponse order,
        McpToolError error
) {

    public static McpOrderResult success(OrderResponse order) {
        return new McpOrderResult(true, order, null);
    }

    public static McpOrderResult unavailable() {
        return new McpOrderResult(
                false,
                null,
                new McpToolError(
                        "ORDER_UNAVAILABLE",
                        "Order unavailable",
                        false
                )
        );
    }
}