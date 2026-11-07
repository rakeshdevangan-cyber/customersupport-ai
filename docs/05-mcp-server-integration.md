# MCP-01 — Secured Spring AI MCP Server

## Objective and outcome
Expose an existing read-only order lookup as an MCP tool without duplicating business logic or trusting LLM-provided customer identity. Local end-to-end invocation from Python LangGraph through Java MCP to PostgreSQL was verified for `ORD-1001`. A cross-customer request for `ORD-2002` was rejected by the ownership-filtered lookup. Java `mvn clean test` was reported passing.

## Stack and ownership
- Java 17, Spring Boot 4.0.8, Spring AI 2.0.1, Spring MVC, Spring Security OAuth2 Resource Server, PostgreSQL, Flyway.
- Dependency: `org.springframework.ai:spring-ai-starter-mcp-server-webmvc` (2.0.1, managed by Spring AI BOM).
- Java owns identity mapping, order ownership authorization, persistence, and MCP server tools. Python owns MCP client and LangGraph orchestration.

## MCP transport and lifecycle
Configuration used in `application.properties`:

```properties
spring.ai.mcp.server.name=customer-support-mcp
spring.ai.mcp.server.version=1.0.0
spring.ai.mcp.server.protocol=STREAMABLE
spring.ai.mcp.server.streamable-http.mcp-endpoint=/mcp
spring.ai.mcp.server.type=SYNC
```

The starter auto-configures the `/mcp` Streamable HTTP transport. No application `@RestController` for `/mcp` is required. The client initializes the protocol session, requests `tools/list`, and invokes `tools/call`. MCP tool names are part of protocol messages; they are not separate REST routes.

## Tool registration
`OrderStatusMcpTool` is a Spring component with an annotated method `@Tool(name = "get_order_status", description = "Retrieve an order's status and estimated delivery date")`. The method accepts only `orderId`.

A `ToolCallbackProvider` bean registers the tool object:

```java
@Bean
ToolCallbackProvider mcpTools(OrderStatusMcpTool orderStatusTool) {
    return MethodToolCallbackProvider.builder()
            .toolObjects(orderStatusTool)
            .build();
}
```

During development a diagnostic `authentication_context_available` tool was registered as well. Startup logged two callbacks, and Python `tools/list` discovered the tool(s). Remove the diagnostic tool before merging.

A warning `No tool methods found in the provided tool objects: []` appeared alongside `Registered tools: 1`; the callback provider was working despite that separate discovery warning.

## Authentication and authorization boundary
- Python sends a customer Keycloak Bearer access token to `/mcp`.
- Spring Security is configured as a stateless JWT resource server; the `/mcp` route requires `SCOPE_orders:read` for this initial order-only server.
- The MCP method checks that `SecurityContextHolder` contains an authenticated `Jwt` principal and verifies required authority. A diagnostic MCP call returned `true`, confirming context availability for the tested synchronous execution path, **not** for all transports or concurrency patterns.
- `CustomerIdentityService.resolveCustomerId(jwt)` maps the verified issuer + subject to an internal customer ID via `CustomerIdentityRepository`.
- `OrderService.getOrder(orderId, customerId)` uses `findByOrderIdAndCustomerId`, enforcing object-level ownership in PostgreSQL.
- Never accept `customerId` from an LLM tool argument. Discovery filtering is not a substitute for tool execution authorization.

## Observed integration tests
1. Unauthenticated POST `/mcp` returned HTTP 401. This alone does not establish endpoint registration.
2. Spring AI auto-configuration and callback registration were visible in startup logs.
3. Python authenticated MCP `initialize` and `tools/list` succeeded.
4. Diagnostic `tools/call` returned `true` for authenticated JWT context.
5. `get_order_status(ORD-1001)` returned a successful order response.
6. With an authenticated `customer-101` session, requesting `ORD-2002` (owned by `customer-202`) produced `is_error=true`. Temporary Java logging confirmed `OrderNotFoundException` at the ownership-filtered lookup.
7. Java Maven compilation and full tests were reported passing. Exact automated test count was not provided.

## Exception handling
An empty ownership-filtered repository result triggers `OrderNotFoundException`. The MCP adapter can convert the thrown exception into a tool-level `CallToolResult` error (`is_error=true`) without an uncaught servlet stack trace. This is different from a REST controller's HTTP 404 mapping. Keep not-found and not-owned indistinguishable to the caller.

## Security and production backlog
- Verify authenticated tool execution and scope checks under concurrent customers and alternate execution models.
- Introduce safe, typed tool-error contracts, tracing, rate limiting and operational metrics.
- Review OAuth protected-resource metadata, token audience and scope policy for each tool.
- Remove temporary diagnostic tool, debug logs, and any secrets in logs.
- Validate that browser cookie authentication is not accepted by the Java Bearer-only API when CSRF is disabled.
- Consider tool-specific authorization when tools with different permissions are added.

## Lessons learned
MCP is a standard tool-discovery and invocation protocol, **not** a replacement for all REST APIs or a substitute for authorization. Our initial transport switch taught protocol integration; MCP-02 will introduce permission-filtered discovery and model-assisted tool selection with an application-controlled execution boundary.

## Next milestone
MCP-02 — Dynamic tool discovery, policy-based filtering, LLM tool selection, argument validation, safe execution and evaluation. Preserve the existing REST path as a supported deterministic adapter.
