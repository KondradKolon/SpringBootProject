package com.project6.ecommerce.mapper;

import com.project6.ecommerce.domain.CreateUserRequest;
import com.project6.ecommerce.domain.dto.CreateUserRequestDto;
import com.project6.ecommerce.domain.dto.UserResponseDto;
import com.project6.ecommerce.domain.entity.User.User;
import com.project6.ecommerce.domain.entity.User.role;
import com.project6.ecommerce.mapper.impl.UserMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapperImpl();
    }

    @Test
    void toDto_ShouldReturnDto_WhenUserExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        User user = new User(id, "Test User", "test@example.com", "password", role.USER);

        // Act
        UserResponseDto dto = userMapper.toDto(user);

        // Assert
        assertNotNull(dto);
        assertEquals(user.getId(), dto.id());
        assertEquals(user.getName(), dto.name());
        assertEquals(user.getEmail(), dto.email());
        assertEquals(user.getRole(), dto.role());
    }

    @Test
    void toDto_ShouldReturnNull_WhenUserIsNull() {
        // Act
        UserResponseDto dto = userMapper.toDto(null);

        // Assert
        assertNull(dto);
    }

    @Test
    void fromDto_ShouldReturnRequest_WhenDtoExists() {
        // Arrange
        CreateUserRequestDto dto = new CreateUserRequestDto(
                "Test User",
                "test@example.com",
                "password",
                role.ADMIN
        );

        // Act
        CreateUserRequest request = userMapper.fromDto(dto);

        // Assert
        assertNotNull(request);
        assertEquals(dto.name(), request.name());
        assertEquals(dto.email(), request.email());
        assertEquals(dto.password(), request.password());
        assertEquals(dto.role(), request.role());
    }

    @Test
    void fromDto_ShouldReturnNull_WhenDtoIsNull() {
        // Act
        CreateUserRequest request = userMapper.fromDto(null);

        // Assert
        assertNull(request);
    }
}
