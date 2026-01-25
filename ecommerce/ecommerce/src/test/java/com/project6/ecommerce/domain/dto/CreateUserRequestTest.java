package com.project6.ecommerce.domain.dto;

import com.project6.ecommerce.domain.entity.User.role;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateUserRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validDto_ShouldPassValidation() {
        CreateUserRequestDto dto = new CreateUserRequestDto("User", "user@example.com", "pass", role.USER);
        Set<ConstraintViolation<CreateUserRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidEmail_ShouldFailValidation() {
        CreateUserRequestDto dto = new CreateUserRequestDto("User", "invalid-email", "pass", role.USER);
        Set<ConstraintViolation<CreateUserRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void emptyName_ShouldFailValidation() {
        CreateUserRequestDto dto = new CreateUserRequestDto("", "user@example.com", "pass", role.USER);
        Set<ConstraintViolation<CreateUserRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }
}
