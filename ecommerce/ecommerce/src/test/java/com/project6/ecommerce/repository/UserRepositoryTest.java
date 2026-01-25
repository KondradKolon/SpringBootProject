package com.project6.ecommerce.repository;

import com.project6.ecommerce.domain.entity.User.User;
import com.project6.ecommerce.domain.entity.User.role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;


import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByName() {
        // GIVEN
        User user = new User();
        user.setName("TestName");
        user.setEmail("test@email.com");
        user.setPassword("pass");
        user.setRole(role.USER);
        userRepository.save(user);

        // WHEN
        Optional<User> found = userRepository.findByName("TestName");

        // THEN
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@email.com");
    }

    @Test
    void shouldCheckIfEmailExists() {
        // GIVEN
        User user = new User();
        user.setName("Jan");
        user.setEmail("exists@test.pl");
        user.setPassword("pass");
        user.setRole(role.USER);
        userRepository.save(user);

        // WHEN
        boolean exists = userRepository.existsByEmail("exists@test.pl");
        boolean notExists = userRepository.existsByEmail("unknown@test.pl");

        // THEN
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}