package com.ridemate.app.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@test.com");
        when(userDetails.getAuthorities())
                .thenAnswer(invocation -> Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void generateToken_ShouldReturnToken() {
        String token = jwtUtil.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_ShouldReturnUsername() {
        String token = jwtUtil.generateToken(userDetails);

        String username = jwtUtil.extractUsername(token);

        assertEquals("test@test.com", username);
    }

    @Test
    void isValid_ShouldReturnTrue_WhenTokenValid() {
        String token = jwtUtil.generateToken(userDetails);

        boolean valid = jwtUtil.isValid(token, userDetails);

        assertTrue(valid);
    }

    @Test
    void isValid_ShouldReturnFalse_WhenUsernameMismatch() {
        String token = jwtUtil.generateToken(userDetails);

        UserDetails differentUser = mock(UserDetails.class);
        when(differentUser.getUsername()).thenReturn("different@test.com");

        boolean valid = jwtUtil.isValid(token, differentUser);

        assertFalse(valid);
    }
}
