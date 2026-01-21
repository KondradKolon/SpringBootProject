package com.project6.ecommerce.mapper;

import com.project6.ecommerce.domain.CreateUserRequest;
import com.project6.ecommerce.domain.dto.CreateUserRequestDto;
import com.project6.ecommerce.domain.dto.UserResponseDto;
import com.project6.ecommerce.domain.entity.User.User;

public interface UserMapper {
    UserResponseDto toDto(User user);
    CreateUserRequest fromDto(CreateUserRequestDto dto);
}
