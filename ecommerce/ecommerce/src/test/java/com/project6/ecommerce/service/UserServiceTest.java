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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) 
class UserServiceTest {

    @Mock 
    private UserRepository userRepository;

    @Mock 
    private PasswordEncoder passwordEncoder;

    @InjectMocks 
    private UserServiceImpl userService;

    @Test
    void shouldCreateUserSuccessfully() {
        
        CreateUserRequest request = new CreateUserRequest("Jan1", "jan1@vp.pl", "pass123", role.USER);

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.createUser(request);

        assertThat(result.getPassword()).isEqualTo("encoded_pass"); 
        verify(userRepository).save(any(User.class)); 
    }

    @Test
    void shouldThrowExceptionWhenEmailTaken() {
        
        CreateUserRequest request = new CreateUserRequest("Jan", "zajety@vp.pl", "pass", role.USER);
        when(userRepository.existsByEmail("zajety@vp.pl")).thenReturn(true);

        Throwable thrown = catchThrowable(() -> userService.createUser(request));

        
        assertThat(thrown).isInstanceOf(RuntimeException.class)
                .hasMessage("User with this email already exists"); 
        verify(userRepository, never()).save(any()); 
    }
}