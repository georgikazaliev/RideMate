package com.ridemate.app.rides.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridemate.app.rides.dto.RideDto;
import com.ridemate.app.security.JwtUtil;
import com.ridemate.app.users.UserRole;
import com.ridemate.app.users.entity.User;
import com.ridemate.app.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RideControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private User driver;
    private String token;

    @BeforeEach
    void setUp() {
        driver = new User();
        driver.setUsername("driver");
        driver.setEmail("driver@example.com");
        driver.setPassword("password");
        driver.setRole(UserRole.DRIVER);
        userRepository.save(driver);

        token = jwtUtil.generateToken(new com.ridemate.app.security.CustomUserDetails(driver));
    }

    @Test
    void createRide_ShouldReturnCreatedRide() throws Exception {
        RideDto rideDto = new RideDto();
        rideDto.setOrigin("Sofia");
        rideDto.setDestination("Plovdiv");
        rideDto.setDateTime(LocalDateTime.now().plusDays(1));
        rideDto.setPrice(20.0);
        rideDto.setSeatsAvailable(3);

        mockMvc.perform(post("/api/v1/rides/")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rideDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.origin").value("Sofia"))
                .andExpect(jsonPath("$.destination").value("Plovdiv"));
    }

    @Test
    void getRides_ShouldReturnList() throws Exception {
        mockMvc.perform(get("/api/v1/rides/")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
