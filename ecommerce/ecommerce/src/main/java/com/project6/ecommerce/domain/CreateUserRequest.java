package com.project6.ecommerce.domain;

import com.project6.ecommerce.domain.entity.User.role;

public record CreateUserRequest(
        String name,
        String email,
        String password,
        role role
) {}
