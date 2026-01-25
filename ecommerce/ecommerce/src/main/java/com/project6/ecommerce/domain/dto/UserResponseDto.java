package com.project6.ecommerce.domain.dto;

import com.project6.ecommerce.domain.entity.User.role;
import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String name,
        String email,
        role role
) {}