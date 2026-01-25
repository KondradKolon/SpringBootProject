package com.project6.ecommerce.domain.entity;

import com.project6.ecommerce.domain.entity.User.User;
import com.project6.ecommerce.domain.entity.User.role;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class UserTest {

    @Test
    void equals_ShouldBeTrue_WhenIdsAreEqual() {
        UUID id = UUID.randomUUID();
        User u1 = new User(id, "Name1", "email@e.com", "pass", role.USER);
        User u2 = new User(id, "Name2", "other@e.com", "pass", role.ADMIN); 

        assertEquals(u1, u2);
        assertEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    void equals_ShouldBeFalse_WhenIdsAreDifferent() {
        User u1 = new User(UUID.randomUUID(), "Name", "e", "p", role.USER);
        User u2 = new User(UUID.randomUUID(), "Name", "e", "p", role.USER);

        assertNotEquals(u1, u2);
    }
}
