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
                                                      @PathVariable Long id){
        return ResponseEntity.ok(new UserResponse(userService.updateUserAsAdmin(currentUser, updateUserRequest, id)));
    }

}
