package com.project6.ecommerce.service;

import com.project6.ecommerce.domain.CreateUserRequest;
import com.project6.ecommerce.domain.entity.User.User;
import com.project6.ecommerce.domain.entity.User.role;
import com.project6.ecommerce.repository.UserRepository;
import com.project6.ecommerce.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_ShouldSaveUser_WhenValid() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest("NewUser", "new@example.com", "pass", role.USER);
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByName(request.name())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            return u;
        });

        // Act
        User result = userService.createUser(request);

        // Assert
        assertNotNull(result);
        assertEquals("NewUser", result.getName());
        assertEquals("encodedPass", result.getPassword());
        assertEquals(role.USER, result.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrow_WhenEmailExists() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest("User", "exist@example.com", "pass", role.USER);
        when(userRepository.existsByEmail("exist@example.com")).thenReturn(true);

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.createUser(request));
        assertEquals("User with this email already exists", ex.getMessage());
        verify(userRepository, never()).save(any());
    }
    
    @Test
    void createUser_ShouldThrow_WhenNameExists() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest("ExistName", "new@example.com", "pass", role.USER);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.existsByName("ExistName")).thenReturn(true);

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.createUser(request));
        assertEquals("User with this name already exists", ex.getMessage());
    }
}
