package com.ridemate.app.users.controller;

import com.ridemate.app.security.CustomUserDetails;
import com.ridemate.app.users.dto.responses.UserResponse;
import com.ridemate.app.users.entity.User;
import com.ridemate.app.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import com.ridemate.app.users.dto.requests.UpdateUserRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getUser(@AuthenticationPrincipal CustomUserDetails currentUser) {
        User user = userService.getUser(currentUser);
        return ResponseEntity.ok(new UserResponse(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> editUser(@AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        return ResponseEntity.ok(new UserResponse(userService.updateUser(currentUser, updateUserRequest)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> editUserAdmin(@AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody UpdateUserRequest updateUserRequest,
            @PathVariable UUID id) {
        return ResponseEntity.ok(new UserResponse(userService.updateUserAsAdmin(currentUser, updateUserRequest, id)));
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateUserRole(@AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id, @RequestParam com.ridemate.app.users.UserRole role) {
        return ResponseEntity.ok(new UserResponse(userService.updateUserRole(currentUser, id, role)));
    }

    @GetMapping("/")
    public ResponseEntity<java.util.List<UserResponse>> getAllUsers(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        java.util.List<UserResponse> users = userService.getAllUsers(currentUser).stream()
                .map(UserResponse::new)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/my-activity")
    public ResponseEntity<java.util.List<com.ridemate.app.client.audit.dto.AuditEntryViewDTO>> getMyActivity(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        return userService.getUserActivity(currentUser);
    }

    @DeleteMapping("/my-activity")
    public ResponseEntity<Void> clearMyActivity(@AuthenticationPrincipal CustomUserDetails currentUser) {
        userService.clearUserActivity(currentUser);
        return ResponseEntity.noContent().build();
    }

}
