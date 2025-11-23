package com.ridemate.app.users.controller;

import com.ridemate.app.users.dto.requests.RefreshRequest;
import com.ridemate.app.users.dto.responses.AuthResponse;
import com.ridemate.app.users.dto.requests.LoginRequest;
import com.ridemate.app.users.dto.requests.RegisterRequest;
import com.ridemate.app.users.dto.responses.RegisterUserResponse;
import com.ridemate.app.users.entity.User;
import com.ridemate.app.users.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> registerUser(
            @Valid @RequestBody RegisterRequest registerRequest
    ) {
        User user = authService.createNewUser(registerRequest);
        return new ResponseEntity<>(new RegisterUserResponse(user.getId()), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
    }
}

