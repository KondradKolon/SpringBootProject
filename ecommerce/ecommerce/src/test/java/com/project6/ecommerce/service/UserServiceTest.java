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

@ExtendWith(MockitoExtension.class) // 1. Włącza Mockito
class UserServiceTest {

    @Mock // Atrapa repozytorium
    private UserRepository userRepository;

    @Mock // Atrapa encodera
    private PasswordEncoder passwordEncoder;

    @InjectMocks // Wstrzykuje atrapy do prawdziwego serwisu
    private UserServiceImpl userService;

    @Test
    void shouldCreateUserSuccessfully() {
        // GIVEN
        CreateUserRequest request = new CreateUserRequest("Jan1", "jan1@vp.pl", "pass123", role.USER);

        // Uczymy makiety jak się zachowywać (when... thenReturn)
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        User result = userService.createUser(request);

        // THEN
        assertThat(result.getPassword()).isEqualTo("encoded_pass"); // Czy zaszyfrowano?
        verify(userRepository).save(any(User.class)); // verify(): Czy save zostało wywołane?
    }

    @Test
    void shouldThrowExceptionWhenEmailTaken() {
        // GIVEN
        CreateUserRequest request = new CreateUserRequest("Jan", "zajety@vp.pl", "pass", role.USER);
        when(userRepository.existsByEmail("zajety@vp.pl")).thenReturn(true);

        // WHEN
        Throwable thrown = catchThrowable(() -> userService.createUser(request));

        // THEN
        assertThat(thrown).isInstanceOf(RuntimeException.class)
                .hasMessage("User with this email already exists"); // Sprawdzamy komunikat błędu
        verify(userRepository, never()).save(any()); // Upewniamy się, że NIE zapisano
    }
}