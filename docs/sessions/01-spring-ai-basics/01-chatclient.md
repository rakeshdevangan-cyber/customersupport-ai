# Exercise 1 — Spring AI ChatClient and Ollama Integration

**Status:** Completed  
**Git milestone:** `s01-e01-chatclient`

## 1. Objective

Build a Spring Boot REST API that accepts a customer question, sends it to a local LLM through Spring AI and returns the generated response.

## 2. Architecture

```text
HTTP Client
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
Ollama HTTP API (localhost:11434)
    |
    v
Llama 3.2
```

## 3. Key Implementation

### Service

```java
@Service
public class SupportChatService {

    private final ChatClient chatClient;

    public SupportChatService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String chat(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
```

This represents the basic ChatClient implementation before introducing dynamic system prompts.

### Important APIs

| API | Purpose |
|---|---|
| `ChatClient.Builder` | Configures and constructs a reusable ChatClient |
| `builder.build()` | Creates the ChatClient |
| `prompt()` | Begins a new model request |
| `user(message)` | Adds the customer's question |
| `call()` | Executes a synchronous model invocation |
| `content()` | Extracts the generated text |

## 4. Internal Execution Flow

1. Spring MVC receives the HTTP request.
2. Jackson deserializes JSON into a Java request record.
3. The controller delegates to `SupportChatService`.
4. ChatClient constructs the model request.
5. Spring AI's Ollama integration sends the request to the local Ollama server.
6. The model generates a response.
7. Spring AI extracts the text and the controller returns JSON.

## 5. Testing

Example PowerShell request:

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/support/chat" -Method POST -ContentType "application/json" -Body '{"message":"Where is my order?"}'
```

**Result:** Successfully received an AI-generated response from Ollama.

## 6. Problems Encountered

### IntelliJ compilation issue

IntelliJ initially reported an unsupported Java 1.5 source level. Restarting IntelliJ resolved the stale project configuration.

### Ollama connection refused

Spring AI initially failed to connect to `localhost:11434` because Ollama was not installed and running.

### Model not found

Ollama returned HTTP 404 because Spring AI requested `mistral`, which was not installed.

The application was configured to use the installed `llama3.2` model.

### PowerShell curl compatibility

Windows PowerShell interpreted `curl` as `Invoke-WebRequest`. Testing was switched to `Invoke-RestMethod`.

## 7. Production Considerations

- LLM inference is an external dependency call.
- Synchronous calls occupy a request thread while waiting for the model.
- Model availability must be checked independently of server availability.
- Production systems require timeouts, monitoring and resilience.
- Generated answers must not be treated as verified business data.

## 8. Interview Questions

**What is ChatClient?**  
Spring AI's higher-level abstraction for constructing prompts and interacting with language models.

**What is the difference between ChatClient and ChatModel?**  
ChatClient provides a convenient fluent interface. ChatModel is the underlying model abstraction that performs inference through a provider integration.

**What happens when `call()` executes?**  
Spring AI invokes the configured model and waits for the response.

**Why use constructor injection?**  
It makes dependencies explicit, supports testing and allows Spring to manage the component lifecycle.

## 9. Git History

[View Exercise 1 source code](https://github.com/rakeshdevangan-cyber/customersupport-ai/tree/s01-e01-chatclient)

This tag preserves the first completed learning milestone.
