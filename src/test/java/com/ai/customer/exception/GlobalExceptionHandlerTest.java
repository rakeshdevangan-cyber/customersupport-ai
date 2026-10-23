package com.ai.customer.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class GlobalExceptionHandlerTest {


    @Test
    public void testException() {

        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleException();

        Assertions.assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        Assertions.assertEquals("AI_CLASSIFICATION_FAILED", response.getBody().get("errorCode"));

        Assertions.assertEquals("Unable to classify the customer request.", response.getBody().get("message"));
    }
}
