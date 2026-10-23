package com.ai.customer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AiClassificationException.class)
    public ResponseEntity<Map<String, String>> handleException() {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                "errorCode", "AI_CLASSIFICATION_FAILED",
                "message", "Unable to classify the customer request."
        ));
    }
}
