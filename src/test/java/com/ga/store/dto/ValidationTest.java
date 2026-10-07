package com.ga.store.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class ValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {

        ValidatorFactory factory =
                Validation.buildDefaultValidatorFactory();

        validator =
                factory.getValidator();
    }

    @Test
    void registerRequestShouldFailWhenPasswordIsTooShort() {

        RegisterRequest request =
                new RegisterRequest(
                        "Test",
                        "User",
                        "test@test.com",
                        "123"
                );

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        assertFalse(
                violations.isEmpty()
        );
    }

    @Test
    void productRequestShouldFailWhenPriceIsZero() {

        ProductRequest request =
                new ProductRequest(
                        "Lavender Soap",
                        "Test soap",
                        BigDecimal.ZERO,
                        10,
                        1L
                );

        Set<ConstraintViolation<ProductRequest>> violations =
                validator.validate(request);

        assertFalse(
                violations.isEmpty()
        );
    }

    @Test
    void productRequestShouldFailWhenStockIsNegative() {

        ProductRequest request =
                new ProductRequest(
                        "Lavender Soap",
                        "Test soap",
                        new BigDecimal("2.50"),
                        -1,
                        1L
                );

        Set<ConstraintViolation<ProductRequest>> violations =
                validator.validate(request);

        assertFalse(
                violations.isEmpty()
        );
    }

    @Test
    void productReviewRequestShouldFailWhenRatingIsAboveFive() {

        ProductReviewRequest request =
                new ProductReviewRequest(
                        6,
                        "Great product"
                );

        Set<ConstraintViolation<ProductReviewRequest>> violations =
                validator.validate(request);

        assertFalse(
                violations.isEmpty()
        );
    }
}