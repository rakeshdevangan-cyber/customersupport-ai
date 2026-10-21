package com.ai.customer.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class SupportChatService {

    private final ChatClient chatClient;;

    public SupportChatService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String chat(String message) {
        // Call the Ollama model and return its response
        return  chatClient.prompt(message).call().content();
    }
}
