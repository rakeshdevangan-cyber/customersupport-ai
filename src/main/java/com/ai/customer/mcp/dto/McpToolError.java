package com.ai.customer.mcp.dto;

public record McpToolError(
        String code,
        String message,
        boolean retryable
) {
}