package com.project6.ecommerce.domain.dto;

import com.project6.ecommerce.domain.entity.Product.status;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

public record CreateProductRequestDto(
        @NotBlank(message = ERROR_MASSAGE_NAME_LENGTH)
        @Length(max =255, message = ERROR_MASSAGE_NAME_LENGTH)
        String name,
        @Length(max = 1000, message = ERROR_MASSAGE_DESCRIPTION_LENGTH)
        @Nullable()
        String description,
        @Min(0)
        double price,
        @Max(10000)
        int quantity,
        Set<Long> categoryIds,
        String image_url,
        @Nonnull
        status status


) {
    private static final String ERROR_MASSAGE_NAME_LENGTH =
            "name must be between 1 and 255 characters";
    private static final String ERROR_MASSAGE_DESCRIPTION_LENGTH =
            "description must have less than 1000 characters";

}
