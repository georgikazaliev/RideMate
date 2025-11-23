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
import org.springframework.security.access.prepost.PreAuthorize;


import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUser(CustomUserDetails currentUser) {
        return userRepository.findById(currentUser.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User updateUser(CustomUserDetails currentUser, UpdateUserRequest updateUserRequest){
        User user = getUser(currentUser);
        return updateUser(user, updateUserRequest);

    }

    public User updateUserAsAdmin(CustomUserDetails currentUser, UpdateUserRequest updateUserRequest, Long id){
        if (!currentUser.getUser().getRole().equals(UserRole.ADMIN)){
            throw new AccessDeniedException("Only admins can do this");
        }
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()){
            throw new ResourceNotFoundException("User not found");
        }
        return updateUser(userOptional.get(), updateUserRequest);
    }

    private User updateUser(User user, UpdateUserRequest updateUserRequest){
        if (updateUserRequest.getEmail() != null && !Objects.equals(user.getEmail(), updateUserRequest.getEmail())){
            if (userRepository.existsByEmail(updateUserRequest.getEmail())) {
                throw new ConflictException("Email already taken");
            }
            user.setEmail(updateUserRequest.getEmail());
        }
        if (updateUserRequest.getUsername() != null && !Objects.equals(user.getUsername(), updateUserRequest.getUsername())){
            if (userRepository.existsByUsername(updateUserRequest.getUsername())){
                throw new ConflictException("Username already taken");
            }
            user.setUsername(updateUserRequest.getUsername());
        }
        if (updateUserRequest.getProfileImageUrl() != null){
            user.setProfileImageUrl(updateUserRequest.getProfileImageUrl());
        }
        return userRepository.save(user);

    }



}
