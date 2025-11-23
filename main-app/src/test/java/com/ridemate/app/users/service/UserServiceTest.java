package com.ridemate.app.users.service;

import com.ridemate.app.client.audit.AuditClient;
import com.ridemate.app.client.audit.dto.CreateAuditEntryDTO;
import com.ridemate.app.exceptions.AccessDeniedException;
import com.ridemate.app.exceptions.ConflictException;
import com.ridemate.app.exceptions.ResourceNotFoundException;
import com.ridemate.app.security.CustomUserDetails;
import com.ridemate.app.users.UserRole;
import com.ridemate.app.users.dto.requests.UpdateUserRequest;
import com.ridemate.app.users.entity.User;
import com.ridemate.app.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditClient auditClient;

    @InjectMocks
    private UserService userService;

    @Test
    void getUser_ShouldReturnUser_WhenFound() {
        CustomUserDetails currentUser = mock(CustomUserDetails.class);
        User user = new User();
        user.setId(UUID.randomUUID());
        when(currentUser.getUser()).thenReturn(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User result = userService.getUser(currentUser);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
    }

    @Test
    void getUser_ShouldThrowException_WhenNotFound() {
        CustomUserDetails currentUser = mock(CustomUserDetails.class);
        User user = new User();
        user.setId(UUID.randomUUID());
        when(currentUser.getUser()).thenReturn(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUser(currentUser));
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUser() {
        CustomUserDetails currentUser = mock(CustomUserDetails.class);
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("old");
        user.setEmail("old@example.com");
        when(currentUser.getUser()).thenReturn(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UpdateUserRequest dto = new UpdateUserRequest();
        dto.setUsername("new");
        dto.setEmail("new@example.com");

        User result = userService.updateUser(currentUser, dto);

        assertNotNull(result);
        verify(auditClient, times(1)).createEntry(any(CreateAuditEntryDTO.class));
    }

    @Test
    void updateUser_ShouldThrowException_WhenEmailTaken() {
        CustomUserDetails currentUser = mock(CustomUserDetails.class);
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("old@test.com");
        when(currentUser.getUser()).thenReturn(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UpdateUserRequest dto = new UpdateUserRequest();
        dto.setEmail("taken@test.com");

        when(userRepository.existsByEmail("taken@test.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.updateUser(currentUser, dto));
    }

    @Test
    void updateUser_ShouldThrowException_WhenUsernameTaken() {
        CustomUserDetails currentUser = mock(CustomUserDetails.class);
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("olduser");
        when(currentUser.getUser()).thenReturn(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UpdateUserRequest dto = new UpdateUserRequest();
        dto.setUsername("takenuser");

        when(userRepository.existsByUsername("takenuser")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.updateUser(currentUser, dto));
    }

    @Test
    void updateUserAsAdmin_ShouldThrowException_WhenNotAdmin() {
        CustomUserDetails currentUser = mock(CustomUserDetails.class);
        User user = new User();
        user.setRole(UserRole.USER);
        when(currentUser.getUser()).thenReturn(user);

        UUID targetUserId = UUID.randomUUID();
        UpdateUserRequest dto = new UpdateUserRequest();

        assertThrows(AccessDeniedException.class, () -> userService.updateUserAsAdmin(currentUser, dto, targetUserId));
    }

    @Test
    void updateUserAsAdmin_ShouldThrowException_WhenTargetNotFound() {
        CustomUserDetails currentUser = mock(CustomUserDetails.class);
        User admin = new User();
        admin.setRole(UserRole.ADMIN);
        when(currentUser.getUser()).thenReturn(admin);

        UUID targetUserId = UUID.randomUUID();
        UpdateUserRequest dto = new UpdateUserRequest();

        when(userRepository.findById(targetUserId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUserAsAdmin(currentUser, dto, targetUserId));
    }
}
