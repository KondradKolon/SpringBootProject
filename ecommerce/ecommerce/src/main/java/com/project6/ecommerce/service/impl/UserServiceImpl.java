package com.project6.ecommerce.service.impl;

import com.project6.ecommerce.domain.CreateUserRequest;
import com.project6.ecommerce.domain.entity.User.User;
import com.project6.ecommerce.domain.entity.User.role;
import com.project6.ecommerce.repository.UserRepository;
import com.project6.ecommerce.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("User with this email already exists");
        }
        if (userRepository.existsByName(request.name())) {
            throw new RuntimeException("User with this name already exists");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(role.USER);

        return userRepository.save(user);
    }
}