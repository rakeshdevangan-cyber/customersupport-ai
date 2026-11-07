package com.ai.customer.config;

import com.ai.customer.mcp.OrderStatusMcpTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class McpToolConfig {

    @Bean
    public ToolCallbackProvider mcpTools(
             OrderStatusMcpTool orderStatusMcpTool) {
        ToolCallbackProvider provider = MethodToolCallbackProvider.builder()
                .toolObjects(orderStatusMcpTool)
                .build();

        log.info(
                "MCP diagnostic tool callbacks: {} ",
                         provider.getToolCallbacks().length
        );

        return provider;
    }
}
