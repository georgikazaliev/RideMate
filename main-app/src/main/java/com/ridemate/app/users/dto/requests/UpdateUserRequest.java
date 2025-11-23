package com.ridemate.app.users.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {

    @Size(min = 3, max = 20)
    private String username;

    @Email
    private String email;

    private String profileImageUrl;
}
