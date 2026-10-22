# Enterprise AI Customer Support Platform — Learning Progress

## Current Status

**Current session:** Session 1 — Spring AI Fundamentals  
**Completed:** Exercises 1, 2 and 3  
**Next exercise:** Exercise 4 — Structured Output  
**Current development branch:** `exercise/01-04-structured-output`

## Completed Exercises

### Exercise 1 — Spring AI ChatClient and REST API

**Status:** Completed

**Implemented:**
- Spring Boot REST endpoint: `POST /api/support/chat`
- `SupportChatController` and `SupportChatService`
- Constructor injection using `ChatClient.Builder`
- Ollama integration with Llama 3.2
- Java records for request and response objects

**Key concepts:**
- Difference between `ChatClient` and `ChatModel`
- `prompt()`, `user()`, `call()` and `content()`
- Synchronous LLM invocation
- How Spring AI communicates with Ollama

**Problems resolved:**
- IntelliJ compiler configuration/cache issue
- Ollama connection refused
- Requested model not installed
- PowerShell curl command compatibility

**Production lesson:** Model inference is an external dependency call and requires timeouts, resilience and monitoring.

**Git tag:** `s01-e01-chatclient`

### Exercise 2 — System Prompts and Business Guardrails

**Status:** Completed

**Implemented:**
- Default system instructions using `ChatClient.Builder`
- Customer support assistant role
- Restrictions on unsupported refunds, reshipments and delivery promises

**Key concepts:**
- System messages versus user messages
- Prompt injection risks
- LLM behavioural instructions
- Difference between prompting and backend authorization

**Production lesson:** System prompts cannot enforce financial or security authorization. Sensitive operations must be validated by trusted Java services.

### Exercise 3 — Dynamic Prompt Templates

**Status:** Completed

**Implemented:**
- Dynamic `{storeName}` and `{tone}` variables
- Request-specific system messages using `.system(...)`
- Template substitution using `.param(...)`
- Updated REST request contract
- Manual testing of model responses

**Key concepts:**
- Prompt rendering
- Dynamic system instructions
- Multi-store AI configuration
- Hallucinations and unsupported operational claims
- Capability grounding

**Production lesson:** Tenant-specific settings must come from trusted configuration. The model must not claim access to capabilities or data that are unavailable.

**Git tag:** `s01-e03-prompt-templates`

## Next Exercise — Structured Output

**Status:** Not started

**Objective:** Convert an LLM response into a strongly typed Java object using Spring AI.

**Planned implementation:**
- Create `SupportClassification` record
- Define category, priority, human-review flag and summary
- Use `ChatClient.entity(SupportClassification.class)` through the call response specification
- Test classification behaviour
- Explore malformed responses and validation

## Upcoming Learning Areas

- Advanced prompt engineering and model options
- Conversation memory and advisors
- Embeddings and semantic similarity
- Document ingestion and RAG
- Vector databases and hybrid retrieval
- Java tool calling
- Agent workflows and orchestration
- MCP and agent interoperability
- Evaluation and automated testing
- Guardrails, security and governance
- OpenTelemetry and production resilience
- Multi-model and multimodal AI
- Kafka-based workflows
- AWS deployment and Terraform
- Enterprise AI architecture and FDE practices

## Engineering Principles

1. Implement one concept at a time.
2. Understand the internal execution flow.
3. Write and review code before progressing.
4. Test normal, edge and failure scenarios.
5. Record architectural decisions and trade-offs.
6. Preserve completed milestones using Git tags.
7. Keep the application evolving toward production readiness.
