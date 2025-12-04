package com.ridemate.app.users.dto.responses;

import lombok.Data;

import java.util.UUID;

@Data
public class RegisterUserResponse {
    private UUID userId;

    public RegisterUserResponse(UUID userId) {
        this.userId = userId;
    }
}
