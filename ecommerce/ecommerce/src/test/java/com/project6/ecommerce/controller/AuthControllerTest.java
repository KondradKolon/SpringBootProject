package com.project6.ecommerce.controller;


import com.project6.ecommerce.domain.dto.CreateUserRequestDto;
import com.project6.ecommerce.domain.entity.User.role; // Twój Enum
import com.project6.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest // 1. Ładuje cały kontekst aplikacji (Baza, Security, Service)
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc // 2. Konfiguruje wirtualnego klienta HTTP
@Transactional // 3. WAŻNE: Po każdym teście cofa zmiany w bazie (rollback)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc; // To nasze narzędzie do strzelania requestami

    @Autowired
    private UserRepository userRepository; // Żeby sprawdzić czy user trafił do bazy
    @Autowired
    private ObjectMapper objectMapper;
 // Żeby zamienić obiekt Java na JSON String

    @Autowired
    private PasswordEncoder passwordEncoder; // Żeby sprawdzić czy hasło jest zakodowane

    @Test
    @DisplayName("Should register user successfully (Happy Path)")
    void shouldRegisterUser() throws Exception {
        // GIVEN - przygotowujemy dane wejściowe (DTO)
        CreateUserRequestDto request = new CreateUserRequestDto(
                "TestUser",            // name
                "test@example.com",    // email
                "Haslo123!",           // password
                role.USER              // role
        );

        // WHEN - wykonujemy strzał POST
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))) // zamiana obiektu na JSON

                // THEN - sprawdzamy odpowiedź HTTP
                .andDo(print()) // Wypisze logi w konsoli (przydatne do debugowania)
                .andExpect(status().isCreated()) // Oczekujemy kodu 201
                .andExpect(jsonPath("$.email").value("test@example.com")) // Czy JSON zwrotny ma email
                .andExpect(jsonPath("$.password").doesNotExist()); // Czy JSON zwrotny NIE MA hasła

        // AND THEN - sprawdzamy czy zapisało w bazie (Integracja)
        var savedUser = userRepository.findByName("TestUser").orElseThrow();

        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getRole()).isEqualTo(role.USER);

        // Sprawdzamy czy hasło w bazie NIE jest tekstem jawnym
        assertThat(savedUser.getPassword()).isNotEqualTo("Haslo123!");
        assertThat(passwordEncoder.matches("Haslo123!", savedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("Should fail when email is invalid (Validation)")
    void shouldFailValidation() throws Exception {
        // GIVEN - błędny email
        CreateUserRequestDto request = new CreateUserRequestDto(
                "TestUser",
                "zly-email", // Błąd
                "Haslo123!",
                role.USER
        );

        // WHEN & THEN
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // Oczekujemy 400 Bad Request
    }
}