package com.ridemate.app.security;

import com.ridemate.app.users.UserRole;
import com.ridemate.app.users.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {

    @Test
    void testGetAuthorities() {
        User user = new User();
        user.setRole(UserRole.DRIVER);

        CustomUserDetails details = new CustomUserDetails(user);
        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_DRIVER")));
    }

    @Test
    void testGetPassword() {
        User user = new User();
        user.setPassword("encodedPassword");

        CustomUserDetails details = new CustomUserDetails(user);

        assertEquals("encodedPassword", details.getPassword());
    }

    @Test
    void testGetUsername() {
        User user = new User();
        user.setEmail("test@test.com");

        CustomUserDetails details = new CustomUserDetails(user);

        assertEquals("test@test.com", details.getUsername());
    }

    @Test
    void testAccountFlags() {
        User user = new User();
        CustomUserDetails details = new CustomUserDetails(user);

        assertTrue(details.isAccountNonExpired());
        assertTrue(details.isAccountNonLocked());
        assertTrue(details.isCredentialsNonExpired());
        assertTrue(details.isEnabled());
    }

    @Test
    void testGetUser() {
        User user = new User();
        user.setEmail("test@test.com");

        CustomUserDetails details = new CustomUserDetails(user);

        assertSame(user, details.getUser());
    }
}
