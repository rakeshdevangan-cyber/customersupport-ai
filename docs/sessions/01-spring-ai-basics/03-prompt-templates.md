# Exercise 3 — Dynamic Prompt Templates

**Status:** Completed  
**Project:** Enterprise AI Customer Support Platform  
**Git milestone:** `s01-e03-prompt-templates`

## 1. Objective

Extend our Spring AI application to support different online stores with configurable assistant behaviour.

Instead of hardcoding one system prompt, dynamically provide:

- Store name
- Response tone
- Customer question

This introduces reusable prompt templates for a multi-store AI application.

## 2. What Is a Prompt Template?

A prompt template contains placeholders that are replaced with values before the prompt is sent to the LLM.

Example:

```text
You are a support assistant for {storeName}.
Respond in a {tone} tone.
```

For example:

- `storeName = Electronics Store`
- `tone = Professional`

The rendered instruction becomes:

```text
You are a support assistant for Electronics Store.
Respond in a Professional tone.
```

The template is rendered by Spring AI before the model receives it.

## 3. Java Implementation

We updated `SupportChatService` to accept three arguments.

```java
public String chat(String message, String storeName, String tone) {
    return chatClient.prompt()
            .system(s -> s
                    .text("You are a support assistant for {storeName}. Respond in a {tone} tone.")
                    .param("storeName", storeName)
                    .param("tone", tone))
            .user(message)
            .call()
            .content();
}
```

This is the initial dynamic-template implementation. The system prompt was subsequently expanded to include business restrictions and capability limitations.

### Important Spring AI APIs

| API | Purpose |
|---|---|
| `prompt()` | Starts a new prompt request |
| `system()` | Defines request-specific system instructions |
| `text()` | Supplies the prompt template |
| `param()` | Provides values for template placeholders |
| `user()` | Adds the customer question |
| `call()` | Invokes the LLM synchronously |
| `content()` | Extracts the generated text |

## 4. REST API Integration

The request record was extended:

```java
public record ChatRequest(
    String message,
    String storeName,
    String tone
) {}
```

The controller delegates to the service:

```java
@PostMapping("/chat")
public ChatResponse chat(@RequestBody ChatRequest message) {
    return new ChatResponse(
        supportChatService.chat(
            message.message(),
            message.storeName(),
            message.tone()
        )
    );
}
```

The response record in the working application uses the field name `response`.

## 5. Internal Execution Flow

```text
Customer HTTP Request
        |
        v
ChatRequest
(message, storeName, tone)
        |
        v
SupportChatController
        |
        v
SupportChatService
        |
        v
Spring AI Template Rendering
{storeName} and {tone} replaced
        |
        v
System Message + User Message
        |
        v
Ollama / Llama 3.2
        |
        v
Generated Response
```

The application reuses the same ChatClient while providing request-specific instructions.

## 6. Testing

PowerShell request:

```powershell
(Invoke-RestMethod -Uri "http://localhost:8080/api/support/chat" -Method POST -ContentType "application/json" -Body '{"message":"My order is delayed. What should I do?","storeName":"Electronics Store","tone":"Professional"}').response
```

### Initial Behaviour

The model generated a professional response but made unsupported commitments, including:

- Offering refunds or store credit.
- Suggesting reshipment.
- Claiming that the team was expediting the order.
- Promising delivery updates.

These claims were not supported by our application's capabilities.

### Prompt Improvements

We strengthened the system instructions to establish that the assistant:

- Cannot access order databases or tracking systems.
- Cannot verify delivery dates.
- Must not promise refunds or reshipments.
- Must not claim that operational actions have been performed.
- Should provide general guidance when verified information is unavailable.

### Final Test Result

The improved model response explicitly acknowledged that it did not have real-time access to order databases or tracking systems.

It offered general guidance without claiming to have checked the customer's order.

**Result:** Passed the manual functional test.

This does not guarantee identical behaviour for every future request.

## 7. Enterprise Architecture Considerations

### Multi-Tenant Configuration

A production application may support hundreds of stores.

Store-specific settings should be retrieved from trusted backend configuration rather than accepted directly from untrusted customer input.

For example:

```text
Authenticated Customer
        |
        v
Resolve Tenant Identity
        |
        v
Load Trusted Store Configuration
        |
        v
Render System Prompt
        |
        v
