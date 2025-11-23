package com.ridemate.app.rides.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridemate.app.rides.dto.RideDto;
import com.ridemate.app.rides.entity.Ride;
import com.ridemate.app.rides.service.RideService;
import com.ridemate.app.security.CustomUserDetails;
import com.ridemate.app.users.entity.User;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RideControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RideService rideService;

    @InjectMocks
    private RideController rideController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        mockMvc = MockMvcBuilders.standaloneSetup(rideController)
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
    void getRides_ShouldReturnList() throws Exception {
        Ride ride = new Ride();
        ride.setId(UUID.randomUUID());
        User driver = new User();
        driver.setId(UUID.randomUUID());
        driver.setUsername("driver");
        ride.setDriver(driver);
        ride.setOrigin("A");
        ride.setDestination("B");
        ride.setDateTime(LocalDateTime.now());
        ride.setPrice(10.0);
        ride.setSeatsAvailable(4);
        ride.setSeatsTaken(0);

        when(rideService.getAllRides(any(CustomUserDetails.class))).thenReturn(Collections.singletonList(ride));

        mockMvc.perform(get("/api/v1/rides/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());

        verify(rideService, times(1)).getAllRides(any(CustomUserDetails.class));
    }

    @Test
    void getRide_ShouldReturnRide() throws Exception {
        UUID rideId = UUID.randomUUID();
        Ride ride = new Ride();
        ride.setId(rideId);
        User driver = new User();
        driver.setId(UUID.randomUUID());
        driver.setUsername("driver");
        ride.setDriver(driver);
        ride.setOrigin("A");
        ride.setDestination("B");
        ride.setDateTime(LocalDateTime.now());
        ride.setPrice(10.0);
        ride.setSeatsAvailable(4);
        ride.setSeatsTaken(0);

        when(rideService.getRideById(rideId)).thenReturn(ride);

        mockMvc.perform(get("/api/v1/rides/{id}", rideId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(rideId.toString()));

        verify(rideService, times(1)).getRideById(rideId);
    }

    @Test
    void createRide_ShouldReturnCreatedRide() throws Exception {
        RideDto rideDto = new RideDto();
        rideDto.setOrigin("A");
        rideDto.setDestination("B");
        rideDto.setDateTime(LocalDateTime.now().plusDays(1));
        rideDto.setPrice(10.0);
        rideDto.setSeatsAvailable(4);

        Ride ride = new Ride();
        ride.setId(UUID.randomUUID());
        User driver = new User();
        driver.setId(UUID.randomUUID());
        driver.setUsername("driver");
        ride.setDriver(driver);
        ride.setOrigin("A");
        ride.setDestination("B");
        ride.setDateTime(rideDto.getDateTime());
        ride.setPrice(10.0);
        ride.setSeatsAvailable(4);
        ride.setSeatsTaken(0);

        when(rideService.createRide(any(CustomUserDetails.class), any(RideDto.class))).thenReturn(ride);

        mockMvc.perform(post("/api/v1/rides/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rideDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        verify(rideService, times(1)).createRide(any(CustomUserDetails.class), any(RideDto.class));
    }

    @Test
    void updateRide_ShouldReturnUpdatedRide() throws Exception {
        UUID rideId = UUID.randomUUID();
        RideDto rideDto = new RideDto();
        rideDto.setOrigin("A");
        rideDto.setDestination("B");
        rideDto.setDateTime(LocalDateTime.now().plusDays(1));
        rideDto.setPrice(10.0);
        rideDto.setSeatsAvailable(4);

        Ride ride = new Ride();
        ride.setId(rideId);
        User driver = new User();
        driver.setId(UUID.randomUUID());
        driver.setUsername("driver");
        ride.setDriver(driver);
        ride.setOrigin("A");
        ride.setDestination("B");
        ride.setDateTime(rideDto.getDateTime());
        ride.setPrice(10.0);
        ride.setSeatsAvailable(4);
        ride.setSeatsTaken(0);

        when(rideService.updateRide(any(CustomUserDetails.class), eq(rideId), any(RideDto.class))).thenReturn(ride);

        mockMvc.perform(put("/api/v1/rides/{id}", rideId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rideDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(rideId.toString()));

        verify(rideService, times(1)).updateRide(any(CustomUserDetails.class), eq(rideId), any(RideDto.class));
    }

    @Test
    void deleteRide_ShouldReturnNoContent() throws Exception {
        UUID rideId = UUID.randomUUID();
        doNothing().when(rideService).deleteRide(any(CustomUserDetails.class), eq(rideId));

        mockMvc.perform(delete("/api/v1/rides/{id}", rideId))
                .andExpect(status().isNoContent());

        verify(rideService, times(1)).deleteRide(any(CustomUserDetails.class), eq(rideId));
    }

    @Test
    void bookRide_ShouldReturnRide() throws Exception {
        UUID rideId = UUID.randomUUID();
        Ride ride = new Ride();
        ride.setId(rideId);
        User driver = new User();
        driver.setId(UUID.randomUUID());
        driver.setUsername("driver");
        ride.setDriver(driver);
        ride.setOrigin("A");
        ride.setDestination("B");
        ride.setDateTime(LocalDateTime.now());
        ride.setPrice(10.0);
        ride.setSeatsAvailable(4);
        ride.setSeatsTaken(1);

        when(rideService.bookRide(any(CustomUserDetails.class), eq(rideId))).thenReturn(ride);

        mockMvc.perform(post("/api/v1/rides/{id}/book", rideId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(rideId.toString()));

        verify(rideService, times(1)).bookRide(any(CustomUserDetails.class), eq(rideId));
    }
}
