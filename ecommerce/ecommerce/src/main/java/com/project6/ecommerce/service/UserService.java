package com.project6.ecommerce.service;

import com.project6.ecommerce.domain.CreateUserRequest;
import com.project6.ecommerce.domain.entity.User.User;

public interface UserService {
    User createUser(CreateUserRequest request);
}
