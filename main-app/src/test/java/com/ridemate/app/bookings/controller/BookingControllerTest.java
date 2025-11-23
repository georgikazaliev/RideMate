package com.ridemate.app.bookings.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridemate.app.bookings.BookingStatus;
import com.ridemate.app.bookings.dto.BookingDto;
import com.ridemate.app.bookings.entity.Booking;
import com.ridemate.app.bookings.service.BookingService;
import com.ridemate.app.rides.entity.Ride;
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

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController)
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
    void createBooking_ShouldReturnCreatedBooking() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setRideId(UUID.randomUUID());

        Booking booking = new Booking();
        booking.setId(UUID.randomUUID());
        booking.setStatus(BookingStatus.PENDING);
        User passenger = new User();
        passenger.setId(UUID.randomUUID());
        passenger.setUsername("passenger");
        booking.setPassenger(passenger);
        Ride ride = new Ride();
        ride.setId(dto.getRideId());
        User driver = new User();
        driver.setId(UUID.randomUUID());
        driver.setUsername("driver");
        ride.setDriver(driver);
        booking.setRide(ride);

        when(bookingService.createBooking(any(CustomUserDetails.class), any(BookingDto.class))).thenReturn(booking);

        mockMvc.perform(post("/api/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId().toString()));

        verify(bookingService, times(1)).createBooking(any(CustomUserDetails.class), any(BookingDto.class));
    }

    @Test
    void getMine_ShouldReturnList() throws Exception {
        Booking booking = new Booking();
        booking.setId(UUID.randomUUID());
        booking.setStatus(BookingStatus.PENDING);
        User passenger = new User();
        passenger.setId(UUID.randomUUID());
        passenger.setUsername("passenger");
        booking.setPassenger(passenger);
        Ride ride = new Ride();
        ride.setId(UUID.randomUUID());
        User driver = new User();
        driver.setId(UUID.randomUUID());
        driver.setUsername("driver");
        ride.setDriver(driver);
        booking.setRide(ride);

        when(bookingService.getMyBookings(any(CustomUserDetails.class))).thenReturn(Collections.singletonList(booking));

        mockMvc.perform(get("/api/v1/bookings/mine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(booking.getId().toString()));

        verify(bookingService, times(1)).getMyBookings(any(CustomUserDetails.class));
    }

    @Test
    void cancelBooking_ShouldReturnCancelledBooking() throws Exception {
        UUID bookingId = UUID.randomUUID();
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.CANCELLED);
        User passenger = new User();
        passenger.setId(UUID.randomUUID());
        passenger.setUsername("passenger");
        booking.setPassenger(passenger);
        Ride ride = new Ride();
        ride.setId(UUID.randomUUID());
        User driver = new User();
        driver.setId(UUID.randomUUID());
        driver.setUsername("driver");
        ride.setDriver(driver);
        booking.setRide(ride);

        when(bookingService.cancelBooking(any(CustomUserDetails.class), eq(bookingId))).thenReturn(booking);

        mockMvc.perform(put("/api/v1/bookings/{id}/cancel", bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        verify(bookingService, times(1)).cancelBooking(any(CustomUserDetails.class), eq(bookingId));
    }

    @Test
    void rejectBooking_ShouldReturnRejectedBooking() throws Exception {
        UUID bookingId = UUID.randomUUID();
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.REJECTED);
        User passenger = new User();
        passenger.setId(UUID.randomUUID());
        passenger.setUsername("passenger");
        booking.setPassenger(passenger);
        Ride ride = new Ride();
        ride.setId(UUID.randomUUID());
        User driver = new User();
        driver.setId(UUID.randomUUID());
        driver.setUsername("driver");
        ride.setDriver(driver);
        booking.setRide(ride);

        when(bookingService.rejectBooking(any(CustomUserDetails.class), eq(bookingId))).thenReturn(booking);

        mockMvc.perform(put("/api/v1/bookings/{id}/reject", bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

        verify(bookingService, times(1)).rejectBooking(any(CustomUserDetails.class), eq(bookingId));
    }

    @Test
    void approveBooking_ShouldReturnApprovedBooking() throws Exception {
        UUID bookingId = UUID.randomUUID();
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.APPROVED);
        User passenger = new User();
        passenger.setId(UUID.randomUUID());
        passenger.setUsername("passenger");
        booking.setPassenger(passenger);
        Ride ride = new Ride();
        ride.setId(UUID.randomUUID());
        User driver = new User();
        driver.setId(UUID.randomUUID());
        driver.setUsername("driver");
        ride.setDriver(driver);
        booking.setRide(ride);

        when(bookingService.approveBooking(any(CustomUserDetails.class), eq(bookingId))).thenReturn(booking);

        mockMvc.perform(put("/api/v1/bookings/{id}/approve", bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingService, times(1)).approveBooking(any(CustomUserDetails.class), eq(bookingId));
    }
}
