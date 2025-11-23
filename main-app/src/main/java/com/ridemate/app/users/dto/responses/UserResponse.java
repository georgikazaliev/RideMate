package com.ridemate.app.users.dto.responses;

import com.ridemate.app.users.UserRole;
import com.ridemate.app.users.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private UserRole role;
    private Double rating;
    private String profileImageUrl;
    private Instant createdAt;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.rating = user.getRating();
        this.profileImageUrl = user.getProfileImageUrl();
        createdAt = user.getCreatedAt();
    }
}
