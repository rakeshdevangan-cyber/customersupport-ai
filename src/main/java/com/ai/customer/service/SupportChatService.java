package com.ai.customer.service;

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

    private final ChatClient chatClient;;

    public SupportChatService(ChatClient.Builder builder) {
        builder.defaultSystem("You are a customer support assistant for an online shopping platform.");
        this.chatClient = builder.build();
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
}
