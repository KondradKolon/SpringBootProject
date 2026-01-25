package com.project6.ecommerce.domain.dto;

import com.project6.ecommerce.domain.entity.Product.status;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateProductRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validDto_ShouldPassValidation() {
        CreateProductRequestDto dto = new CreateProductRequestDto(
                "Valid Name",
                "Description",
                BigDecimal.valueOf(10.0),
                5,
                Set.of(1L),
                "img.jpg",
                status.AVAILABLE
        );
        Set<ConstraintViolation<CreateProductRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidName_ShouldFailValidation() {
        // Name blank
        CreateProductRequestDto dto = new CreateProductRequestDto(
                "",
                "Description",
                BigDecimal.valueOf(10.0),
                5,
                null,
                null,
                status.AVAILABLE
        );
        Set<ConstraintViolation<CreateProductRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void invalidPrice_ShouldFailValidation() {
        // Price negative
        CreateProductRequestDto dto = new CreateProductRequestDto(
                "Name",
                "Desc",
                BigDecimal.valueOf(-10.0),
                5,
                null,
                null,
                status.AVAILABLE
        );
        Set<ConstraintViolation<CreateProductRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }
}
