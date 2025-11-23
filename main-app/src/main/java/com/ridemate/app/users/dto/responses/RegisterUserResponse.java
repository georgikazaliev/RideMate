package com.ridemate.app.users.dto.responses;

import java.util.UUID;

public class RegisterUserResponse {

    private String message;
    private UUID userId;

    public RegisterUserResponse(UUID userId) {
        this.message = "User registered successfully.";
        this.userId = userId;
    }

}
