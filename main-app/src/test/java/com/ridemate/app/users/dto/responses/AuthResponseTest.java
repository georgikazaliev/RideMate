package com.ridemate.app.users.dto.responses;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthResponseTest {

    @Test
    void testConstructorAndGetters() {
        AuthResponse response = new AuthResponse("accessToken123", "refreshToken456");

        assertEquals("accessToken123", response.getAccessToken());
        assertEquals("refreshToken456", response.getRefreshToken());
    }

    @Test
    void testSetters() {
        AuthResponse response = new AuthResponse("old", "old");
        response.setAccessToken("newAccess");
        response.setRefreshToken("newRefresh");

        assertEquals("newAccess", response.getAccessToken());
        assertEquals("newRefresh", response.getRefreshToken());
    }
}
