package com.ai.customer.service;

import com.ai.customer.dto.SupportClassification;
import com.ai.customer.exception.AiClassificationException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
public class SupportChatService {

    private final String systemMessage = "1.The assistant must never claim to have checked an order unless verified order data is available." +
            "2.It must not promise refunds, reshipments, return labels or delivery dates." +
            "3.It must not claim that operational actions have been performed." +
            "4.When order information is unavailable, it should explain the limitation and ask for the order number." +
            "5.It should remain professional and concise." +
            "6.You do not have access to order databases, tracking systems or delivery forecasts. Never claim that you can check an order, retrieve its status or provide a delivery estimate. " +
            "You may explain how customers can check their orders through the official website.";

    private final String structuredMessage = "1. classify customer support messages.";

    private final ChatClient chatClient;

    private final SupportClassificationValidator supportClassificationValidator;

    public SupportChatService(ChatClient.Builder builder,
                              SupportClassificationValidator supportClassificationValidator) {
        builder.defaultSystem("You are a customer support assistant for an online shopping platform.");
        this.chatClient = builder.build();
        this.supportClassificationValidator = supportClassificationValidator;
    }

    public String chat(String message, String storeName, String tone) {
        // Call the Ollama model and return its response
        return  chatClient.prompt().system(
                s-> s.text(systemMessage)
                        .param("storeName", storeName)
                        .param("tone", tone))
                .user(message)
                .call()
                .content();
    }

    public SupportClassification classify(String message) {
        // Your implementation
        String instructions = """
        You are a customer support classification assistant.
        
        Priority rules: HIGH for urgent payment disputes, MEDIUM for ordinary delivery problems, LOW for general information requests.
        
        Human escalation: Set requiresHuman=true when a payment dispute requires investigation or a refund requires authorization.
        
        Summary: Generate a short factual summary of the customer's issue without inventing information.

        Allowed categories:
        DELIVERY, PAYMENT, REFUND, ACCOUNT, OTHER.

        Allowed priorities:
        LOW, MEDIUM, HIGH.
        
        Output requirements:
        Return only a valid JSON object.
        Use exactly these fields:
        category, priority, requiresHuman, summary.
                
        category and priority must be strings.
        requiresHuman must be a JSON true or false.
        summary must be a string.
                
        Do not include Markdown, explanations, or code fences.
        """;
        SupportClassification supportClassification =  chatClient.prompt()
                .system(s-> s.text(instructions))
                .user(message)
                .call()
                .entity(SupportClassification.class);
                //.content();

        try {
            return supportClassificationValidator.validate(supportClassification);
        } catch(IllegalArgumentException e) {
            throw new AiClassificationException(e.getMessage());
        }
    }
}
