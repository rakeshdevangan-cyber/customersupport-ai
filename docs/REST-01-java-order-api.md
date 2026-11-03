# REST-01 — Secure Java Order API and Python LangGraph Integration

## Objective

Implement a secure Java Spring Boot Order API that can be called by a Python LangGraph agent using Keycloak OAuth2 authentication.

## Architecture

Customer → Python FastAPI → LangGraph → Java Spring Boot → PostgreSQL

**Java responsibilities**
- JWT validation and scope authorization.
- Keycloak identity-to-customer mapping.
- Order ownership enforcement.
- Database transactions and business APIs.

**Python responsibilities**
- OAuth2 Authorization Code + PKCE.
- Authenticated sessions.
- LangGraph orchestration and tool execution.
- HTTPX REST integration and error handling.

## Implementation

- Spring Security OAuth2 Resource Server.
- Keycloak issuer and audience validation.
- `orders:read` scope enforcement.
- CustomerIdentity entity and repository.
- CustomerIdentityService resolving verified JWT issuer and subject.
- OrderService ownership-filtered retrieval.
- OrderController exposing `GET /api/orders/{orderId}`.
- Flyway V1 migration for customer business tables.
- Standardized HTTP error handling using ProblemDetail.

## Security Design

1. Never trust customer IDs supplied by the browser, LLM or Python agent.
2. Validate JWT signature, issuer, audience and expiry.
3. Resolve the verified Keycloak subject to an internal customer ID.
4. Restrict order retrieval to the authenticated customer's records.
5. Return 404 for missing or inaccessible orders to avoid revealing resource existence.
6. Return 403 for valid but unmapped customer identities.

## Verified Manual Tests

| Scenario | Result |
|---|---|
| Customer-101 retrieves ORD-1001 | Passed |
| Customer-202 attempts ORD-1001 | Safely rejected |
| Missing JWT | 401 |
| Unknown customer identity | Java 403 |
| Java unavailable | Python returns a safe fallback |
| Python → Java → PostgreSQL | Successful |
| Maven test build | Successful |

## Production Considerations

- Use managed PostgreSQL and Keycloak persistence.
- Introduce automated integration tests with Testcontainers.
- Use structured logging and distributed tracing.
- Keep secrets in a secret manager.
- Add token refresh and session-expiry handling.
- Add user-level authorization policies.
- Audit customer identity provisioning.
- Use least-privilege permissions for AI tool execution.

## Remaining Work

- Automated security and REST integration regression tests.
- Dynamic order-ID extraction edge-case tests.
- User-level permission testing.
- CI/CD integration.
- Production observability and performance testing.

## Next Milestone

Implement Java MCP server capabilities and connect the Python LangGraph MCP client, reusing the existing OrderService rather than duplicating business logic.
