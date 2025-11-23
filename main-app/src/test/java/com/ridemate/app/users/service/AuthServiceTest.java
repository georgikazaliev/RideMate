package com.ridemate.app.users.service;

import com.ridemate.app.security.CustomUserDetailsService;
import com.ridemate.app.security.JwtUtil;
import com.ridemate.app.users.dto.requests.LoginRequest;
import com.ridemate.app.users.dto.requests.RegisterRequest;
import com.ridemate.app.users.dto.responses.AuthResponse;
import com.ridemate.app.users.entity.RefreshToken;
import com.ridemate.app.users.entity.User;
import com.ridemate.app.users.repository.RefreshTokenRepository;
import com.ridemate.app.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void createNewUser_ShouldCreateUser_WhenValid() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("new@test.com");
        request.setPassword("password123");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setUsername("newuser");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = authService.createNewUser(request);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createNewUser_ShouldThrowException_WhenUsernameExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existing");
        request.setEmail("new@test.com");

        when(userRepository.existsByUsername("existing")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.createNewUser(request));
    }

    @Test
    void login_ShouldReturnAuthResponse_WhenValid() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("password");

        User user = new User();
        user.setEmail("test@test.com");

        UserDetails userDetails = mock(UserDetails.class);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername("test@test.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("accessToken");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void refresh_ShouldReturnNewTokens_WhenValid() {
        String tokenValue = "validRefreshToken";

        User user = new User();
        user.setEmail("test@test.com");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(tokenValue);
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(1));

        UserDetails userDetails = mock(UserDetails.class);

        when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(refreshToken));
        when(userDetailsService.loadUserByUsername("test@test.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("newAccessToken");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = authService.refresh(tokenValue);

        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
    }

    @Test
    void refresh_ShouldThrowException_WhenTokenExpired() {
        String tokenValue = "expiredToken";

        User user = new User();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(tokenValue);
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.now().minusDays(1));

        when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(refreshToken));

        assertThrows(RuntimeException.class, () -> authService.refresh(tokenValue));
        verify(refreshTokenRepository, times(1)).delete(refreshToken);
    }
}
