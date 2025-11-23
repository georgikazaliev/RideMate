package com.ridemate.app.users.dto.responses;

public class RegisterUserResponse {

    private String message;
    private Long userId;

    public RegisterUserResponse(Long userId) {
        this.message = "User registered successfully.";
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
