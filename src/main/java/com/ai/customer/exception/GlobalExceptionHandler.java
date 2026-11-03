package com.ai.customer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
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

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleOrderNotFound(
            OrderNotFoundException ex) {

        ProblemDetail problem = ProblemDetail.forStatus(
                HttpStatus.NOT_FOUND);

        problem.setTitle("Order not found");
        problem.setDetail(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(problem);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUserPermitted(
            UserNotFoundException ex) {

        ProblemDetail problem = ProblemDetail.forStatus(
                HttpStatus.FORBIDDEN);

        problem.setTitle("Access denied");
        problem.setDetail(
                "You are not authorized to access this resource."
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(problem);
    }
}
