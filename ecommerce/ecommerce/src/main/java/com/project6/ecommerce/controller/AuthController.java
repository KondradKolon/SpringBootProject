package com.project6.ecommerce.controller;

import com.project6.ecommerce.domain.CreateUserRequest;
import com.project6.ecommerce.domain.dto.CreateUserRequestDto;
import com.project6.ecommerce.domain.dto.UserResponseDto;
import com.project6.ecommerce.domain.entity.User.User;
import com.project6.ecommerce.mapper.UserMapper;
import com.project6.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final UserMapper userMapper;
    public AuthController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(
            @Valid @RequestBody CreateUserRequestDto createUserRequestDto
    ) {
        CreateUserRequest createUserRequest = userMapper.fromDto(createUserRequestDto);
        User user = userService.createUser(createUserRequest);
        UserResponseDto userResponseDto = userMapper.toDto(user);
        return new ResponseEntity<>(userResponseDto, HttpStatus.CREATED);
    }
}