package com.ridemate.app.rides.service;

import com.ridemate.app.bookings.entity.Booking;
import com.ridemate.app.bookings.repository.BookingRepository;
import com.ridemate.app.client.audit.AuditClient;
import com.ridemate.app.client.audit.dto.CreateAuditEntryDTO;
import com.ridemate.app.exceptions.AccessDeniedException;
import com.ridemate.app.exceptions.ConflictException;
import com.ridemate.app.exceptions.ResourceNotFoundException;
import com.ridemate.app.rides.RideStatus;
import com.ridemate.app.rides.dto.RideDto;
import com.ridemate.app.rides.entity.Ride;
import com.ridemate.app.rides.repository.RideRepository;
import com.ridemate.app.security.CustomUserDetails;
import com.ridemate.app.users.entity.User;
import com.ridemate.app.users.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RideServiceTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private AuditClient auditClient;

    @InjectMocks
    private RideService rideService;

    @Test
    void getAllRides_ShouldReturnList() {
        CustomUserDetails currentUser = mock(CustomUserDetails.class);
        User user = new User();
        user.setId(UUID.randomUUID());
        when(userService.getUser(currentUser)).thenReturn(user);
        when(rideRepository.findByStatusAndDateTimeAfter(any(RideStatus.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        List<Ride> result = rideService.getAllRides(currentUser);

        assertNotNull(result);
        verify(rideRepository, times(1)).findByStatusAndDateTimeAfter(any(RideStatus.class), any(LocalDateTime.class));
    }

    @Test
    void getRideById_ShouldReturnRide_WhenFound() {
        UUID rideId = UUID.randomUUID();
        Ride ride = new Ride();
        ride.setId(rideId);
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));

        Ride result = rideService.getRideById(rideId);

        assertNotNull(result);
        assertEquals(rideId, result.getId());
    }

    @Test
    void getRideById_ShouldThrowException_WhenNotFound() {
        UUID rideId = UUID.randomUUID();
        when(rideRepository.findById(rideId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> rideService.getRideById(rideId));
    }

    @Test
    void createRide_ShouldSaveAndReturnRide() {
        CustomUserDetails currentUser = mock(CustomUserDetails.class);
        User user = new User();
        user.setId(UUID.randomUUID());
        when(userService.getUser(currentUser)).thenReturn(user);

        RideDto rideDto = new RideDto();
        rideDto.setOrigin("A");
        rideDto.setDestination("B");
        rideDto.setDateTime(LocalDateTime.now());
        rideDto.setPrice(10.0);
        rideDto.setSeatsAvailable(4);

        Ride savedRide = new Ride();
        savedRide.setId(UUID.randomUUID());
        when(rideRepository.save(any(Ride.class))).thenReturn(savedRide);

        Ride result = rideService.createRide(currentUser, rideDto);

        assertNotNull(result);
        verify(rideRepository, times(1)).save(any(Ride.class));
        verify(auditClient, times(1)).createEntry(any(CreateAuditEntryDTO.class));
    }

    @Test
    void updateRide_ShouldUpdateAndReturnRide() {
        CustomUserDetails currentUser = mock(CustomUserDetails.class);
        User user = new User();
        user.setId(UUID.randomUUID());
        when(userService.getUser(currentUser)).thenReturn(user);

        UUID rideId = UUID.randomUUID();
        Ride ride = new Ride();
        ride.setId(rideId);
        ride.setDriver(user);
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(rideRepository.save(any(Ride.class))).thenReturn(ride);

        RideDto rideDto = new RideDto();
        rideDto.setOrigin("New Origin");
        rideDto.setDestination("New Dest");
        rideDto.setDateTime(LocalDateTime.now());
        rideDto.setPrice(20.0);
        rideDto.setSeatsAvailable(5);

        Ride result = rideService.updateRide(currentUser, rideId, rideDto);

        assertNotNull(result);
        assertEquals("New Origin", result.getOrigin());
        verify(auditClient, times(1)).createEntry(any(CreateAuditEntryDTO.class));
    }

    @Test
    void updateRide_ShouldThrowAccessDenied_WhenNotDriver() {
        CustomUserDetails currentUser = mock(CustomUserDetails.class);
        User user = new User();
        user.setId(UUID.randomUUID());
        when(userService.getUser(currentUser)).thenReturn(user);

        UUID rideId = UUID.randomUUID();
        Ride ride = new Ride();
        ride.setId(rideId);
        User otherDriver = new User();
        otherDriver.setId(UUID.randomUUID());
        ride.setDriver(otherDriver);
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));

        RideDto rideDto = new RideDto();

        assertThrows(AccessDeniedException.class, () -> rideService.updateRide(currentUser, rideId, rideDto));
    }

    @Test
    void deleteRide_ShouldDelete_WhenDriver() {
        CustomUserDetails currentUser = mock(CustomUserDetails.class);
        User user = new User();
        user.setId(UUID.randomUUID());
        when(userService.getUser(currentUser)).thenReturn(user);

        UUID rideId = UUID.randomUUID();
        Ride ride = new Ride();
        ride.setId(rideId);
        ride.setDriver(user);
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));

        rideService.deleteRide(currentUser, rideId);

        verify(rideRepository, times(1)).delete(ride);
        verify(auditClient, times(1)).createEntry(any(CreateAuditEntryDTO.class));
    }

    @Test
    void bookRide_ShouldBook_WhenSeatsAvailable() {
        CustomUserDetails currentUser = mock(CustomUserDetails.class);
        User user = new User();
        user.setId(UUID.randomUUID());
        when(userService.getUser(currentUser)).thenReturn(user);

        UUID rideId = UUID.randomUUID();
        Ride ride = new Ride();
        ride.setId(rideId);
        ride.setSeatsAvailable(4);
        ride.setSeatsTaken(0);
        ride.setBookings(Collections.emptyList());
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(rideRepository.save(any(Ride.class))).thenReturn(ride);

        Ride result = rideService.bookRide(currentUser, rideId);

        assertNotNull(result);
        assertEquals(1, result.getSeatsTaken());
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(auditClient, times(1)).createEntry(any(CreateAuditEntryDTO.class));
    }

    @Test
    void bookRide_ShouldThrowConflict_WhenNoSeats() {
        CustomUserDetails currentUser = mock(CustomUserDetails.class);
        User user = new User();
        user.setId(UUID.randomUUID());
        when(userService.getUser(currentUser)).thenReturn(user);

        UUID rideId = UUID.randomUUID();
        Ride ride = new Ride();
        ride.setId(rideId);
        ride.setSeatsAvailable(4);
        ride.setSeatsTaken(4);
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));

        assertThrows(ConflictException.class, () -> rideService.bookRide(currentUser, rideId));
    }

    @Test
    void bookRide_ShouldThrowException_WhenAlreadyBooked() {
        CustomUserDetails currentUser = mock(CustomUserDetails.class);
        User user = new User();
        user.setId(UUID.randomUUID());
        when(userService.getUser(currentUser)).thenReturn(user);

        UUID rideId = UUID.randomUUID();
        Ride ride = new Ride();
        ride.setId(rideId);
        ride.setSeatsAvailable(4);
        ride.setSeatsTaken(1);

        Booking existingBooking = new Booking();
        existingBooking.setPassenger(user);
        existingBooking.setStatus(com.ridemate.app.bookings.BookingStatus.APPROVED);
        ride.setBookings(Collections.singletonList(existingBooking));

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));

        assertThrows(ConflictException.class, () -> rideService.bookRide(currentUser, rideId));
    }

    @Test
    void deleteRide_ShouldThrowException_WhenNotDriver() {
        CustomUserDetails currentUser = mock(CustomUserDetails.class);
        User user = new User();
        user.setId(UUID.randomUUID());
        when(userService.getUser(currentUser)).thenReturn(user);

        UUID rideId = UUID.randomUUID();
        Ride ride = new Ride();
        User otherDriver = new User();
        otherDriver.setId(UUID.randomUUID());
        ride.setDriver(otherDriver);
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));

        assertThrows(AccessDeniedException.class, () -> rideService.deleteRide(currentUser, rideId));
    }
}
