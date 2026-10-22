package com.ai.customer.rest;


import com.ai.customer.model.ChatRequest;
import com.ai.customer.model.ChatResponse;
import com.ai.customer.service.SupportChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/support")
public class SupportChatController {

    private final SupportChatService supportChatService;

    public SupportChatController(SupportChatService supportChatService) {
        this.supportChatService = supportChatService;
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest message) {
        return new ChatResponse(supportChatService.chat(message.message(), message.storeName(), message.tone()));
    }
}
