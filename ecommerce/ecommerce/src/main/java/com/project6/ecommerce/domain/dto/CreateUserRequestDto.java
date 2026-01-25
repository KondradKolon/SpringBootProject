package com.project6.ecommerce.domain.dto;


import com.project6.ecommerce.domain.entity.User.role;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CreateUserRequestDto(
        @NotBlank(message = ERROR_MESSAGE_NAME_LENGTH)
        @Length(max =16, message = ERROR_MESSAGE_NAME_LENGTH)
        String name,
        @NotBlank(message = ERROR_MESSAGE_EMAIL_LENGTH)
        @Email(message = ERROR_MESSAGE_EMAIL_VALID)
        String email,
        @NotBlank(message = ERROR_MESSAGE_PASSWORD_VALID)
        String password,
        @Nonnull
        role role


) {
    private static final String ERROR_MESSAGE_NAME_LENGTH =
            "name must be between 1 and 255 characters";
    private static final String ERROR_MESSAGE_EMAIL_LENGTH =
            "must enter email";
    private static final String ERROR_MESSAGE_EMAIL_VALID =
            "email must be a valid email try again";
    private static final String ERROR_MESSAGE_PASSWORD_VALID =
            "Enter a valid password";

}
