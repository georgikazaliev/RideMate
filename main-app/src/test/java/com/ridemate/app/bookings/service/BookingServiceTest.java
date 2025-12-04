package com.ridemate.app.bookings.service;

import com.ridemate.app.bookings.BookingStatus;
import com.ridemate.app.bookings.dto.BookingDto;
import com.ridemate.app.bookings.entity.Booking;
import com.ridemate.app.bookings.repository.BookingRepository;
import com.ridemate.app.client.audit.AuditClient;
import com.ridemate.app.client.audit.dto.CreateAuditEntryDTO;
import com.ridemate.app.exceptions.AccessDeniedException;
import com.ridemate.app.exceptions.ConflictException;
import com.ridemate.app.exceptions.ResourceNotFoundException;
import com.ridemate.app.rides.entity.Ride;
import com.ridemate.app.rides.repository.RideRepository;
import com.ridemate.app.security.CustomUserDetails;
import com.ridemate.app.users.entity.User;
import com.ridemate.app.users.service.UserService;
import com.ridemate.app.users.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserService userService;

    @Mock
    private RideRepository rideRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private AuditClient auditClient;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void createBooking_ShouldCreate_WhenSeatsAvailable() {
        CustomUserDetails currentUser = mock(CustomUserDetails.class);
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setRole(UserRole.USER);
        when(userService.getUser(currentUser)).thenReturn(user);

        UUID rideId = UUID.randomUUID();
        BookingDto bookingDto = new BookingDto();
        bookingDto.setRideId(rideId);

        Ride ride = new Ride();
        ride.setId(rideId);
        ride.setSeatsAvailable(4);
        ride.setSeatsTaken(0);
        User driver = new User();
        driver.setId(UUID.randomUUID());
        ride.setDriver(driver);
        ride.setDateTime(java.time.LocalDateTime.now().plusDays(1));
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));

        Booking savedBooking = new Booking();
        savedBooking.setId(UUID.randomUUID());
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        Booking result = bookingService.createBooking(currentUser, bookingDto);

        assertNotNull(result);
        assertEquals(1, ride.getSeatsTaken());
        verify(auditClient, times(1)).createEntry(any(CreateAuditEntryDTO.class));
    }

    @Test
    void createBooking_ShouldThrowConflict_WhenNoSeats() {
        CustomUserDetails currentUser = mock(CustomUserDetails.class);
        User user = new User();
        user.setRole(UserRole.USER);
        when(userService.getUser(currentUser)).thenReturn(user);

        UUID rideId = UUID.randomUUID();
        BookingDto bookingDto = new BookingDto();
        bookingDto.setRideId(rideId);

        Ride ride = new Ride();
        ride.setSeatsAvailable(4);
        ride.setSeatsTaken(4);
        User driver = new User();
        driver.setId(UUID.randomUUID());
        ride.setDriver(driver);
        ride.setDateTime(java.time.LocalDateTime.now().plusDays(1));
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));

        assertThrows(ConflictException.class, () -> bookingService.createBooking(currentUser, bookingDto));
    }

    @Test
    void getMyBookings_ShouldReturnList() {
        CustomUserDetails currentUser = mock(CustomUserDetails.class);
        User user = new User();
        user.setId(UUID.randomUUID());
        when(userService.getUser(currentUser)).thenReturn(user);
        when(bookingRepository.findByPassengerId(user.getId())).thenReturn(Collections.emptyList());

        List<Booking> result = bookingService.getMyBookings(currentUser);

        assertNotNull(result);
        verify(bookingRepository, times(1)).findByPassengerId(user.getId());
    }

    @Test
    void cancelBooking_ShouldCancel_WhenPassenger() {
        CustomUserDetails currentUser = mock(CustomUserDetails.class);
        User user = new User();
        user.setId(UUID.randomUUID());
        when(userService.getUser(currentUser)).thenReturn(user);

        UUID bookingId = UUID.randomUUID();
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setPassenger(user);
        Ride ride = new Ride();
        ride.setSeatsTaken(1);
        booking.setRide(ride);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking result = bookingService.cancelBooking(currentUser, bookingId);

        assertNotNull(result);
        assertEquals(BookingStatus.CANCELLED, result.getStatus());
        assertEquals(0, ride.getSeatsTaken());
        verify(auditClient, times(1)).createEntry(any(CreateAuditEntryDTO.class));
    }

    @Test
    void rejectBooking_ShouldReject_WhenDriver() {
        CustomUserDetails currentUser = mock(CustomUserDetails.class);
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setRole(UserRole.DRIVER);
        when(userService.getUser(currentUser)).thenReturn(user);

        UUID bookingId = UUID.randomUUID();
        Booking booking = new Booking();
        booking.setId(bookingId);
        Ride ride = new Ride();
        ride.setDriver(user);
        ride.setSeatsTaken(1);
        booking.setRide(ride);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking result = bookingService.rejectBooking(currentUser, bookingId);

        assertNotNull(result);
        assertEquals(BookingStatus.REJECTED, result.getStatus());
        assertEquals(0, ride.getSeatsTaken());
        verify(auditClient, times(1)).createEntry(any(CreateAuditEntryDTO.class));
    }

    @Test
    void approveBooking_ShouldApprove_WhenDriver() {
        CustomUserDetails currentUser = mock(CustomUserDetails.class);
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setRole(UserRole.DRIVER);
        when(userService.getUser(currentUser)).thenReturn(user);

        UUID bookingId = UUID.randomUUID();
        Booking booking = new Booking();
        booking.setId(bookingId);
        Ride ride = new Ride();
        ride.setDriver(user);
        booking.setRide(ride);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking result = bookingService.approveBooking(currentUser, bookingId);

        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(auditClient, times(1)).createEntry(any(CreateAuditEntryDTO.class));
    }

    @Test
    void createBooking_ShouldThrowException_WhenRideNotFound() {
        CustomUserDetails currentUser = mock(CustomUserDetails.class);
        User user = new User();
        user.setRole(UserRole.USER);
        when(userService.getUser(currentUser)).thenReturn(user);

        UUID rideId = UUID.randomUUID();
        BookingDto bookingDto = new BookingDto();
        bookingDto.setRideId(rideId);

        when(rideRepository.findById(rideId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookingService.createBooking(currentUser, bookingDto));
    }

    @Test
    void cancelBooking_ShouldThrowException_WhenNotPassenger() {
        CustomUserDetails currentUser = mock(CustomUserDetails.class);
        User user = new User();
        user.setId(UUID.randomUUID());
        when(userService.getUser(currentUser)).thenReturn(user);

        UUID bookingId = UUID.randomUUID();
        Booking booking = new Booking();
        User otherPassenger = new User();
        otherPassenger.setId(UUID.randomUUID());
        booking.setPassenger(otherPassenger);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(AccessDeniedException.class, () -> bookingService.cancelBooking(currentUser, bookingId));
    }

    @Test
    void rejectBooking_ShouldThrowException_WhenNotDriver() {
        CustomUserDetails currentUser = mock(CustomUserDetails.class);
        User user = new User();
        user.setId(UUID.randomUUID());
        when(userService.getUser(currentUser)).thenReturn(user);

        UUID bookingId = UUID.randomUUID();
        Booking booking = new Booking();
        Ride ride = new Ride();
        User otherDriver = new User();
        otherDriver.setId(UUID.randomUUID());
        ride.setDriver(otherDriver);
        booking.setRide(ride);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(AccessDeniedException.class, () -> bookingService.rejectBooking(currentUser, bookingId));
    }

    @Test
    void approveBooking_ShouldThrowException_WhenNotDriver() {
        CustomUserDetails currentUser = mock(CustomUserDetails.class);
        User user = new User();
        user.setId(UUID.randomUUID());
        when(userService.getUser(currentUser)).thenReturn(user);

        UUID bookingId = UUID.randomUUID();
        Booking booking = new Booking();
        Ride ride = new Ride();
        User otherDriver = new User();
        otherDriver.setId(UUID.randomUUID());
        ride.setDriver(otherDriver);
        booking.setRide(ride);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(AccessDeniedException.class, () -> bookingService.approveBooking(currentUser, bookingId));
    }
}
