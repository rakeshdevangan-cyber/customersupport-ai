# Enterprise AI Customer Support Platform

A hands-on enterprise AI engineering project built with **Java, Spring Boot, Spring AI and Ollama**.

## Project Objective

Build a production-oriented AI customer support platform, evolving from a simple LLM-powered REST API into an enterprise AI application with RAG, vector databases, tool calling, AI agents, MCP, evaluation, security, observability and AWS deployment.

The repository documents each learning milestone, implementation decisions, testing results and production considerations.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java | Backend development |
| Spring Boot | REST APIs and application framework |
| Spring AI | LLM integration and orchestration |
| Ollama | Local LLM serving |
| Llama 3.2 | Language model |
| Maven | Build and dependency management |
| GitHub | Version control and learning milestones |

## Current Architecture

```text
Customer / PowerShell
        |
        v
POST /api/support/chat
        |
        v
SupportChatController
        |
        v
SupportChatService
        |
        v
Spring AI ChatClient
        |
        v
Ollama HTTP API
        |
        v
Llama 3.2
```

## Learning Milestones

| Exercise | Topic | Status |
|---|---|---|
| 1 | ChatClient, REST API and Ollama | Completed |
| 2 | System prompts and basic guardrails | Completed |
| 3 | Dynamic prompt templates | Completed |
| 4 | Structured output with Java records | Next |
| 5 | Conversation memory | Planned |
| 6 | Embeddings and RAG | Planned |
| 7 | Vector databases and retrieval | Planned |
| 8 | Java tool calling | Planned |
| 9 | AI agents and MCP | Planned |
| 10 | Evaluation and security | Planned |
| 11 | Observability and resilience | Planned |
| 12 | Kafka and AWS deployment | Planned |

The full curriculum is organized into 15 learning sessions, with advanced topics covered progressively.

## Completed Code Milestones

- [Exercise 1 — ChatClient and Ollama](https://github.com/rakeshdevangan-cyber/customersupport-ai/tree/s01-e01-chatclient)
- [Exercises 2–3 — System Prompts and Dynamic Templates](https://github.com/rakeshdevangan-cyber/customersupport-ai/tree/s01-e03-prompt-templates)
- [Compare completed milestones](https://github.com/rakeshdevangan-cyber/customersupport-ai/compare/s01-e01-chatclient...s01-e03-prompt-templates)

## Run Locally

### Prerequisites

- Compatible JDK and Maven
- Ollama installed
- Llama 3.2 downloaded

### Start Ollama

```powershell
ollama pull llama3.2
ollama serve
```

If Ollama is already running in the background, a second server is unnecessary.

### Start Spring Boot

```powershell
mvn spring-boot:run
```

### Test the Chat API

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/support/chat" -Method POST -ContentType "application/json" -Body '{"message":"Where is my order?","storeName":"Electronics Store","tone":"Professional"}'
```

## Enterprise Engineering Principles

This project progressively demonstrates:

- Separation of REST APIs, business services and model integrations.
- Reusable and configurable LLM clients.
- Prompt templates and business-specific assistant behaviour.
- Prevention of unsupported operational commitments.
- Secure authorization of business actions.
- Model evaluation, reliability and observability.
- Cloud deployment and operational readiness.

**Important:** Prompt instructions alone cannot guarantee security, factual correctness or authorization. These controls must be enforced and evaluated at the application level.

## Development Approach

Each exercise is developed on a feature branch, reviewed, tested and merged into `main`.

Completed exercises are preserved using Git tags. Documentation explains the changes and the engineering decisions behind them.

This enables reviewers to inspect the evolution of the application rather than only its final implementation.
