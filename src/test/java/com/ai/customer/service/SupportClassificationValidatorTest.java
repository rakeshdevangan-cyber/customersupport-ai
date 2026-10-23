package com.ai.customer.service;

import com.ai.customer.dto.SupportClassification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SupportClassificationValidatorTest {

    @InjectMocks
    private SupportClassificationValidator testee;

    @Test
    public void testSuccess() {
        SupportClassification input =
                new SupportClassification(
                        "PAYMENT",
                        "LOW",
                        false,
                        "Customer was charged twice"
                );

        SupportClassification output =  testee.validate(input);

        Assertions.assertTrue(output.requiresHuman());
        Assertions.assertEquals("HIGH", output.priority());
        Assertions.assertEquals(input.category(), output.category());
        Assertions.assertEquals(input.summary(), output.summary());
    }

    @Test
    public void testDefaultValues() {
        SupportClassification input =
                new SupportClassification(
                        "DELIVERY",
                        "LOW",
                        false,
                        "Customer wants general delivery information"
                );

        SupportClassification output =  testee.validate(input);

        Assertions.assertFalse(output.requiresHuman());
        Assertions.assertEquals("LOW", output.priority());
        Assertions.assertEquals(input.category(), output.category());
        Assertions.assertEquals(input.summary(), output.summary());
    }

    @Test
    public void responseValueFailure() {
        SupportClassification input = new SupportClassification(
                null,
                "LOW",
                false,
                "Customer reports an issue"
        );

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> testee.validate(input)
        );

    }

    @Test
    public void responseValueFailure_WhenClassificationNull() {
        SupportClassification input =null;

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> testee.validate(input)
        );

    }
}
