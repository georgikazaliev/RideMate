package com.ridemate.app.users.dto.responses;

import com.ridemate.app.users.UserRole;
import com.ridemate.app.users.entity.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserResponseTest {

    @Test
    void testConstructorAndGetters() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setRole(UserRole.DRIVER);

        UserResponse response = new UserResponse(user);

        assertEquals(user.getId(), response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@test.com", response.getEmail());
        assertEquals(UserRole.DRIVER, response.getRole());
    }

    @Test
    void testNoArgsConstructor() {
        UserResponse response = new UserResponse();
        assertNull(response.getId());
    }
}
