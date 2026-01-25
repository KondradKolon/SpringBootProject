package com.project6.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project6.ecommerce.domain.CreateUserRequest;
import com.project6.ecommerce.domain.dto.CreateUserRequestDto;
import com.project6.ecommerce.domain.dto.UserResponseDto;
import com.project6.ecommerce.domain.entity.User.User;
import com.project6.ecommerce.domain.entity.User.role;
import com.project6.ecommerce.mapper.UserMapper;
import com.project6.ecommerce.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration 
    static class Config {
        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @Test
    void registerUser_ShouldReturnCreated_WhenValid() throws Exception {
        // Arrange
        CreateUserRequestDto requestDto = new CreateUserRequestDto("NewUser", "new@example.com", "pass", role.USER);
        CreateUserRequest request = new CreateUserRequest("NewUser", "new@example.com", "pass", role.USER);
        User user = new User(UUID.randomUUID(), "NewUser", "new@example.com", "pass", role.USER);
        UserResponseDto responseDto = new UserResponseDto(user.getId(), "NewUser", "new@example.com", role.USER);

        when(userMapper.fromDto(any(CreateUserRequestDto.class))).thenReturn(request);
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(user);
        when(userMapper.toDto(any(User.class))).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("NewUser"))
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    void registerUser_ShouldReturnBadRequest_WhenInvalid() throws Exception {
        // Arrange - empty name/email
        CreateUserRequestDto invalidDto = new CreateUserRequestDto("", "invalid-email", "", role.USER);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest()); 
    }
}