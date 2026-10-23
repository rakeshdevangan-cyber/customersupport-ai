package com.ai.customer.service;

import com.ai.customer.dto.SupportClassification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SupportClassificationValidator {


    public SupportClassification validate(
            SupportClassification classification) {

        if(classification == null || !StringUtils.hasText(classification.category())) {
            throw new IllegalArgumentException("Classification and category must not be null or blank");
        }
        // Your implementation
        if("PAYMENT".equals(classification.category())) {
            return new SupportClassification(classification.category(),
                    "HIGH",
                    true,
                     classification.summary());
        }
        return classification;
    }
}
