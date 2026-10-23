package com.ai.customer.dto;

public record SupportClassification(String category,
                                    String priority,
                                    boolean requiresHuman,
                                    String summary) {
}
