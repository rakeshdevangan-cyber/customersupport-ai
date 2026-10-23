# Exercise 4 — Structured Output and AI Classification Validation

**Status:** Completed  
**Project:** Enterprise AI Customer Support Platform  
**Git milestone:** `s01-e04-structured-output`

## 1. Objective

Extend the Spring AI application to classify customer support requests into structured Java objects instead of returning only free-form text.

The classification includes:

- Category: DELIVERY, PAYMENT, REFUND, ACCOUNT or OTHER
- Priority: LOW, MEDIUM or HIGH
- Human escalation requirement
- Short factual summary

## 2. Architecture

```text
POST /api/support/classify
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
Ollama / Llama 3.2
          |
          v
.entity(SupportClassification.class)
          |
          v
SupportClassificationValidator
          |
          v
Validated JSON Response
```

When classification validation fails, the service translates the failure into `AiClassificationException`, which is handled centrally by `GlobalExceptionHandler`.

## 3. Structured Output with Java Records

```java
public record SupportClassification(
    String category,
    String priority,
    boolean requiresHuman,
    String summary
) {}
```

Spring AI's `.entity(SupportClassification.class)` converts the model's structured response into this Java record.

Unlike `.content()`, which returns text, `.entity()` provides a typed Java result.

## 4. Classification Prompt

The classification prompt defines:

- Allowed categories and priorities.
- Priority selection rules.
- Human escalation rules.
- Factual summary requirements.
- JSON output formatting requirements.

The model is instructed to return a JSON object with exactly four fields, without Markdown or explanatory text.

## 5. Structured Output Failure Encountered

During implementation, Spring AI initially failed with a Jackson `UnexpectedEndOfInputException`.

To investigate, we temporarily changed the method from `.entity()` to `.content()`.

The raw LLM response contained Markdown-style classification information rather than valid JSON.

After adding explicit JSON formatting instructions, the model generated a valid JSON response.

We then restored `.entity(SupportClassification.class)` and successfully converted the response into a Java record.

**Lesson:** Semantic correctness does not guarantee structural correctness.

## 6. Business Validation

Created `SupportClassificationValidator` to enforce deterministic Java business rules.

For the current lab:

- PAYMENT classifications must have HIGH priority.
- PAYMENT classifications must require human review.
- Other valid classifications remain unchanged.
- Null classifications and missing or blank categories are rejected.

The validator returns a new record when a correction is required because Java records are immutable.

This is a simplified learning rule and will need refinement for production use.

## 7. Exception Handling

Created `AiClassificationException` to distinguish invalid AI classification results from ordinary customer input errors.

Created `GlobalExceptionHandler` using `@RestControllerAdvice` and `@ExceptionHandler`.

Classification validation failures return:

**HTTP status:** 502 Bad Gateway

```json
{
  "errorCode": "AI_CLASSIFICATION_FAILED",
  "message": "Unable to classify the customer request."
}
```

The response avoids exposing internal validation details to API consumers.

## 8. Testing

Implemented the following tests:

| Test | Purpose |
|---|---|
| PAYMENT validation | Ensures HIGH priority and human escalation |
| Non-PAYMENT validation | Verifies unchanged classifications |
| Null category | Verifies validation exception |
| Null classification | Verifies validation exception |
| GlobalExceptionHandler unit test | Verifies HTTP 502 and error fields |
| MockMvc controller test | Verifies Spring MVC exception routing |

**Result:** Full Maven test suite passed.

## 9. Enterprise AI Engineering Lessons

### Structural correctness

The LLM response must be convertible into the required Java type.

### Business correctness

A valid Java record can still contain incorrect business decisions. Trusted Java rules must enforce critical constraints.

### API reliability

Downstream AI failures must be handled through predictable API responses rather than exposing raw stack traces.

### Testing strategy

Deterministic business logic should be tested independently of LLM inference.

MockMvc can test HTTP behaviour without requiring Ollama.

## 10. Remaining Production Improvements

- Validate allowed category and priority values.
- Consider nullable escalation fields and explicit missing-field validation.
- Handle malformed JSON conversion failures separately.
- Handle model-provider outages and timeouts.
- Introduce native structured-output support.
- Add automated classification evaluation datasets.
- Improve observability and tracing.
- Refine business rules for different payment scenarios.

## 11. Interview Questions

**What is structured output in Spring AI?**

It allows an LLM response to be converted into a defined Java type using APIs such as `.entity()`.

**Does successful deserialization guarantee a correct classification?**

No. It verifies structural compatibility, not business correctness.

**Why introduce a Java validator after the LLM?**

To enforce deterministic business rules independently of model-generated recommendations.

**Why use a custom exception?**

To distinguish AI processing failures and map them to consistent API error responses.

**Why use MockMvc?**

To test Spring MVC request handling and exception resolution without starting Ollama or a real HTTP server.

## 12. Git Milestone

**Branch:** `exercise/01-04-structured-output`  
**Tag:** `s01-e04-structured-output` — to be created after merging the exercise pull request.
