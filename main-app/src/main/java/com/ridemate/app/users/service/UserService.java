package com.ridemate.app.users.service;

import com.ridemate.app.exceptions.AccessDeniedException;
import com.ridemate.app.exceptions.ConflictException;
import com.ridemate.app.exceptions.ResourceNotFoundException;
import com.ridemate.app.security.CustomUserDetails;
import com.ridemate.app.users.dto.requests.UpdateUserRequest;
import com.ridemate.app.users.entity.User;
import com.ridemate.app.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ridemate.app.users.UserRole;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.ridemate.app.client.audit.AuditClient auditClient;

    public User getUser(CustomUserDetails currentUser) {
        return userRepository.findById(currentUser.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User updateUser(CustomUserDetails currentUser, UpdateUserRequest updateUserRequest) {
        User user = getUser(currentUser);
        return updateUser(user, updateUserRequest);
    }

    public User updateUserAsAdmin(CustomUserDetails currentUser, UpdateUserRequest updateUserRequest, UUID id) {
        if (!currentUser.getUser().getRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("Only admins can do this");
        }
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        return updateUser(userOptional.get(), updateUserRequest);
    }

    public User updateUserRole(CustomUserDetails currentUser, UUID id, UserRole newRole) {
        if (!currentUser.getUser().getRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("Only admins can do this");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setRole(newRole);
        User savedUser = userRepository.save(user);

        try {
            auditClient.createEntry(new com.ridemate.app.client.audit.dto.CreateAuditEntryDTO(
                    currentUser.getUser().getId(),
                    "UPDATE_ROLE",
                    "USER",
                    savedUser.getId(),
                    "User role updated to " + newRole));
        } catch (Exception e) {
            System.err.println("Failed to create audit entry: " + e.getMessage());
        }
        return savedUser;
    }

    public java.util.List<User> getAllUsers(CustomUserDetails currentUser) {
        if (!currentUser.getUser().getRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("Only admins can do this");
        }
        return userRepository.findAll();
    }

    public org.springframework.http.ResponseEntity<java.util.List<com.ridemate.app.client.audit.dto.AuditEntryViewDTO>> getUserActivity(
            CustomUserDetails currentUser) {
        return auditClient.getEntriesForUser(currentUser.getUser().getId());
    }

    public void clearUserActivity(CustomUserDetails currentUser) {
        auditClient.deleteEntriesForUser(currentUser.getUser().getId());
    }

    private User updateUser(User user, UpdateUserRequest updateUserRequest) {
        if (updateUserRequest.getEmail() != null && !Objects.equals(user.getEmail(), updateUserRequest.getEmail())) {
            if (userRepository.existsByEmail(updateUserRequest.getEmail())) {
                throw new ConflictException("Email already taken");
            }
            user.setEmail(updateUserRequest.getEmail());
        }
        if (updateUserRequest.getUsername() != null
                && !Objects.equals(user.getUsername(), updateUserRequest.getUsername())) {
            if (userRepository.existsByUsername(updateUserRequest.getUsername())) {
                throw new ConflictException("Username already taken");
            }
            user.setUsername(updateUserRequest.getUsername());
        }
        if (updateUserRequest.getProfileImageUrl() != null) {
            user.setProfileImageUrl(updateUserRequest.getProfileImageUrl());
        }
        User savedUser = userRepository.save(user);
        try {
            auditClient.createEntry(new com.ridemate.app.client.audit.dto.CreateAuditEntryDTO(
                    user.getId(),
                    "UPDATE",
                    "USER",
                    savedUser.getId(),
                    "User updated profile"));
        } catch (Exception e) {
            System.err.println("Failed to create audit entry: " + e.getMessage());
        }
        return savedUser;
    }
}
