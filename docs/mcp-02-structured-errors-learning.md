# MCP-02 Java — Structured MCP Order Results

**Repository:** `customersupport-ai`  
**Feature branch:** `feature/mcp-02-structured-errors`  
**Milestone:** MCP-02, Java component  
**Status:** Implemented and unit-tested; branch merge not yet confirmed

## 1. Goal

Provide a predictable, safe application-level result contract for the existing secure `get_order_status` MCP tool, distinguishing successful order retrieval from an unavailable order without exposing another customer's information.

## 2. Ownership and architecture

Java owns business data, order lookup, authenticated customer mapping, authorization enforcement, and MCP result serialization. Python owns tool discovery, orchestration, client-side parsing, and customer-facing responses.

Flow: authenticated MCP call → JWT principal and `SCOPE_orders:read` checks → `CustomerIdentityService.resolveCustomerId(jwt)` → `OrderService.getOrder(orderId, customerId)` → `McpOrderResult`.

The service/repository's ownership-filtered lookup remains the authoritative business-data boundary. The LLM cannot supply a trusted customer ID.

## 3. Implementation

### Result records

- `com.ai.customer.mcp.dto.McpToolError`: `code`, `message`, `retryable`.
- `com.ai.customer.mcp.dto.McpOrderResult`: `success`, `order`, `error`, with `success(OrderResponse)` and `unavailable()` factories.
- `OrderStatusMcpTool.getOrderStatus(String orderId)` now returns `McpOrderResult`.

### Success contract

```json
{"success":true,"order":{"orderId":"ORD-1001","status":"SHIPPED","estimatedDelivery":"2026-11-10"},"error":null}
```

### Unavailable-order contract

```json
{"success":false,"order":null,"error":{"code":"ORDER_UNAVAILABLE","message":"Order unavailable","retryable":false}}
```

Both nonexistent and other-customer orders receive the same application error code. `OrderNotFoundException` is caught only around the order lookup and mapped to `McpOrderResult.unavailable()`.

Authentication and scope failures continue to raise `AccessDeniedException`. Unexpected service/database errors are not converted into `ORDER_UNAVAILABLE`.

## 4. MCP wire-format observation

In the local integration test, Spring AI returned the JSON envelope in an MCP text content block with `is_error=false` and `structured_content=null`. This is an **application-level** success/error envelope, not a standard MCP protocol error type. Python must inspect the envelope's `success` and `error.code` fields.

## 5. Tests and verification

Reported passing in the Java branch:

- `McpOrderResultTest`: success and unavailable factories.
- `OrderStatusMcpToolTest`: owned-order success; cross-customer unavailable result; existing authentication/scope security assertions.
- Full `mvn clean test` regression suite.

Live integration observations:

- Owned `ORD-1001` returned `SHIPPED` and estimated delivery `2026-11-10`.
- An order unavailable to the authenticated customer produced `ORDER_UNAVAILABLE`; no protected order data was returned to the client.

These are development-test observations, not a production security certification.

## 6. Security decisions

1. Use JWT principal and authorities from Spring Security, never user-supplied customer identity.
2. Enforce `orders:read` and ownership at Java execution time.
3. Use the same unavailable result for nonexistent and other-customer orders to avoid existence disclosure.
4. Do not serialize JWTs, customer IDs, stack traces, database errors, or raw exception details into tool results.
5. Do not translate authentication failures or unexpected infrastructure failures into a successful business envelope.

## 7. Limitations and deferred work

- The envelope currently models `ORDER_UNAVAILABLE` explicitly; broader typed errors such as `SERVICE_UNAVAILABLE` require an agreed contract and tests.
- Verify serialization and compatibility whenever Spring AI/MCP versions change.
- Extend integration/security tests before production deployment; unit tests alone do not exercise every transport or database boundary.
- Remove any obsolete diagnostic tools and sensitive debug logging only after checking references and tests.

## 8. Commands for final branch verification

```powershell
mvn clean test
git diff --check
git status
git diff --stat
```

Do not commit generated secrets, local credentials, `.env` files, or unrelated untracked Maven Wrapper files without review.

## 9. Next milestone

After merging Java and Python MCP-02 branches, begin MCP-03 multi-tool orchestration. Java adds business tools and per-tool authorization; Python handles selection, bounded execution, orchestration, and evaluation.
