package com.ridemate.app.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridemate.app.security.CustomUserDetails;
import com.ridemate.app.users.dto.requests.UpdateUserRequest;
import com.ridemate.app.users.entity.User;
import com.ridemate.app.users.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().equals(CustomUserDetails.class);
                    }

                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
                        User user = new User();
                        user.setId(UUID.randomUUID());
                        user.setUsername("testuser");
                        return new CustomUserDetails(user);
                    }
                })
                .build();
    }

    @Test
    void getUser_ShouldReturnUser() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        when(userService.getUser(any(CustomUserDetails.class))).thenReturn(user);

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(userService, times(1)).getUser(any(CustomUserDetails.class));
    }

    @Test
    void editUser_ShouldReturnUpdatedUser() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");

        User updatedUser = new User();
        updatedUser.setId(UUID.randomUUID());
        updatedUser.setUsername("newuser");
        updatedUser.setEmail("new@example.com");

        when(userService.updateUser(any(CustomUserDetails.class), any(UpdateUserRequest.class)))
                .thenReturn(updatedUser);

        mockMvc.perform(put("/api/v1/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newuser"));

        verify(userService, times(1)).updateUser(any(CustomUserDetails.class), any(UpdateUserRequest.class));
    }

    @Test
    void editUserAdmin_ShouldReturnUpdatedUser() throws Exception {
        UUID userId = UUID.randomUUID();
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("newuser");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUsername("newuser");

        when(userService.updateUserAsAdmin(any(CustomUserDetails.class), any(UpdateUserRequest.class), eq(userId)))
                .thenReturn(updatedUser);

        mockMvc.perform(put("/api/v1/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newuser"));

        verify(userService, times(1)).updateUserAsAdmin(any(CustomUserDetails.class), any(UpdateUserRequest.class),
                eq(userId));
    }
}
