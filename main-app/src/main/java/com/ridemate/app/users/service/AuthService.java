package com.ridemate.app.users.service;

import com.ridemate.app.security.CustomUserDetailsService;
import com.ridemate.app.security.JwtUtil;
import com.ridemate.app.users.UserRole;
import com.ridemate.app.users.dto.requests.LoginRequest;
import com.ridemate.app.users.dto.requests.RegisterRequest;
import com.ridemate.app.users.dto.responses.AuthResponse;
import com.ridemate.app.users.entity.RefreshToken;
import com.ridemate.app.users.entity.User;
import com.ridemate.app.users.repository.RefreshTokenRepository;
import com.ridemate.app.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createNewUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername()) ||
                userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Username or Email already exists.");
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        try {
            if (registerRequest.getRole() != null && !registerRequest.getRole().isEmpty()) {
                user.setRole(UserRole.valueOf(registerRequest.getRole().toUpperCase()));
            } else {
                user.setRole(UserRole.USER);
            }
        } catch (IllegalArgumentException e) {
            user.setRole(UserRole.USER);
        }
        user.setRating(0.0);

        return userRepository.save(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()));

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = generateRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken);
    }

    private String generateRefreshToken(User user) {
        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.flush();
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(LocalDateTime.now().plus(30, ChronoUnit.DAYS));

        refreshTokenRepository.save(token);

        return token.getToken();
    }

    public AuthResponse refresh(String refreshTokenValue) {
        RefreshToken token = refreshTokenRepository
                .findByToken(refreshTokenValue)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired");
        }

        User user = token.getUser();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String newAccessToken = jwtService.generateToken(userDetails);
        String newRefreshToken = generateRefreshToken(user);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }
}
