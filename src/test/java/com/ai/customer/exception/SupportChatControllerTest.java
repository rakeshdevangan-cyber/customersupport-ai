package com.ai.customer.exception;

import com.ai.customer.rest.SupportChatController;
import com.ai.customer.service.SupportChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SupportChatControllerTest {

    private MockMvc mockMvc;
    private SupportChatService supportChatService;

    @BeforeEach
    void setUp() {
        supportChatService = mock(SupportChatService.class);

        SupportChatController controller =
                new SupportChatController(supportChatService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturn502WhenAiClassificationFails() throws Exception {

        when(supportChatService.classify(anyString()))
                .thenThrow(new AiClassificationException(
                        "Invalid AI classification"));

        mockMvc.perform(post("/api/support/classify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "message": "I was charged twice"
                                }
                                """))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.errorCode")
                        .value("AI_CLASSIFICATION_FAILED"))
                .andExpect(jsonPath("$.message")
                        .value("Unable to classify the customer request."));
    }
}
