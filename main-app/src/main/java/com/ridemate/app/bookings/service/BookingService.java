package com.ridemate.app.bookings.service;

import com.ridemate.app.bookings.BookingStatus;
import com.ridemate.app.bookings.dto.BookingDto;
import com.ridemate.app.bookings.entity.Booking;
import com.ridemate.app.bookings.repository.BookingRepository;
import com.ridemate.app.exceptions.AccessDeniedException;
import com.ridemate.app.exceptions.ConflictException;
import com.ridemate.app.exceptions.ResourceNotFoundException;
import com.ridemate.app.rides.entity.Ride;
import com.ridemate.app.security.CustomUserDetails;
import com.ridemate.app.users.entity.User;
import com.ridemate.app.users.service.UserService;
import com.ridemate.app.rides.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private com.ridemate.app.client.audit.AuditClient auditClient;

    @Transactional
    @CacheEvict(value = { "rides", "ride" }, allEntries = true)
    public Booking createBooking(CustomUserDetails currentUser, BookingDto bookingDto) {
        User user = userService.getUser(currentUser);
        if (user.getRole() != com.ridemate.app.users.UserRole.USER) {
            throw new AccessDeniedException("Only passengers can book rides");
        }
        Ride ride = rideRepository.findById(bookingDto.getRideId())
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));
        if (ride.getDriver().getId().equals(user.getId())) {
            throw new ConflictException("Cannot book your own ride");
        }
        if (ride.getDateTime().isBefore(java.time.LocalDateTime.now())) {
            throw new ConflictException("Cannot book a ride in the past");
        }
        if (ride.getSeatsTaken() >= ride.getSeatsAvailable()) {
            throw new ConflictException("No seats available");
        }
        Booking booking = new Booking();
        booking.setPassenger(user);
        booking.setRide(ride);
        booking.setStatus(BookingStatus.PENDING);
        ride.setSeatsTaken(ride.getSeatsTaken() + 1);
        rideRepository.save(ride);
        Booking savedBooking = bookingRepository.save(booking);
        try {
            auditClient.createEntry(new com.ridemate.app.client.audit.dto.CreateAuditEntryDTO(
                    user.getId(),
                    "CREATE",
                    "BOOKING",
                    savedBooking.getId(),
                    "Booking created"));
        } catch (Exception e) {
            System.err.println("Failed to create audit entry: " + e.getMessage());
        }
        return savedBooking;
    }

    public List<Booking> getMyBookings(CustomUserDetails currentUser) {
        User user = userService.getUser(currentUser);
        return bookingRepository.findByPassengerId(user.getId());
    }

    public List<Booking> getBookingRequests(CustomUserDetails currentUser) {
        User user = userService.getUser(currentUser);
        return bookingRepository.findByRideDriverId(user.getId());
    }

    @CacheEvict(value = { "rides", "ride" }, allEntries = true)
    public Booking cancelBooking(CustomUserDetails currentUser, UUID id) {
        User user = userService.getUser(currentUser);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!booking.getPassenger().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to cancel this booking");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        Ride ride = booking.getRide();
        ride.setSeatsTaken(ride.getSeatsTaken() - 1);
        rideRepository.save(ride);
        Booking savedBooking = bookingRepository.save(booking);
        try {
            auditClient.createEntry(new com.ridemate.app.client.audit.dto.CreateAuditEntryDTO(
                    user.getId(),
                    "CANCEL",
                    "BOOKING",
                    savedBooking.getId(),
                    "Booking cancelled"));
        } catch (Exception e) {
            System.err.println("Failed to create audit entry: " + e.getMessage());
        }
        return savedBooking;
    }

    @Transactional
    @CacheEvict(value = { "rides", "ride" }, allEntries = true)
    public Booking rejectBooking(CustomUserDetails currentUser, UUID id) {
        User user = userService.getUser(currentUser);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (user.getRole() != com.ridemate.app.users.UserRole.DRIVER) {
            throw new AccessDeniedException("Only drivers can reject bookings");
        }
        if (!booking.getRide().getDriver().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to reject this booking");
        }
        booking.setStatus(BookingStatus.REJECTED);
        Ride ride = booking.getRide();
        ride.setSeatsTaken(ride.getSeatsTaken() - 1);
        rideRepository.save(ride);
        Booking savedBooking = bookingRepository.save(booking);

        try {
            auditClient.createEntry(new com.ridemate.app.client.audit.dto.CreateAuditEntryDTO(
                    user.getId(),
                    "REJECT",
                    "BOOKING",
                    savedBooking.getId(),
                    "Booking rejected"));
        } catch (Exception e) {
            System.err.println("Failed to create audit entry: " + e.getMessage());
        }
        return savedBooking;
    }

    @Transactional
    @CacheEvict(value = { "rides", "ride" }, allEntries = true)
    public Booking approveBooking(CustomUserDetails currentUser, UUID id) {
        User user = userService.getUser(currentUser);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (user.getRole() != com.ridemate.app.users.UserRole.DRIVER) {
            throw new AccessDeniedException("Only drivers can approve bookings");
        }
        if (!booking.getRide().getDriver().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to approve this booking");
        }
        booking.setStatus(BookingStatus.APPROVED);
        Booking savedBooking = bookingRepository.save(booking);

        try {
            auditClient.createEntry(new com.ridemate.app.client.audit.dto.CreateAuditEntryDTO(
                    user.getId(),
                    "APPROVE",
                    "BOOKING",
                    savedBooking.getId(),
                    "Booking approved"));
        } catch (Exception e) {
            System.err.println("Failed to create audit entry: " + e.getMessage());
        }
        return savedBooking;
    }
}
