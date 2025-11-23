package com.ridemate.app.rides.service;

import com.ridemate.app.bookings.BookingStatus;
import com.ridemate.app.bookings.entity.Booking;
import com.ridemate.app.bookings.repository.BookingRepository;
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
import com.ridemate.app.client.audit.AuditClient;
import com.ridemate.app.client.audit.dto.CreateAuditEntryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RideService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private AuditClient auditClient;

    @Cacheable(value = "rides", key = "#currentUser.username")
    public List<Ride> getAllRides(CustomUserDetails currentUser) {
        User user = userService.getUser(currentUser);
        List<Ride> allRides = rideRepository.findByStatusAndDateTimeAfter(RideStatus.ACTIVE,
                java.time.LocalDateTime.now());

        return allRides.stream()
                .filter(ride -> ride.getBookings().stream()
                        .noneMatch(booking -> booking.getPassenger().getId().equals(user.getId()) &&
                                (booking.getStatus() == BookingStatus.PENDING
                                        || booking.getStatus() == BookingStatus.APPROVED)))
                .collect(java.util.stream.Collectors.toList());
    }

    @Cacheable(value = "ride", key = "#id")
    public Ride getRideById(UUID id) {
        return rideRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ride not found"));
    }

    @CacheEvict(value = { "rides", "ride" }, allEntries = true)
    public Ride createRide(CustomUserDetails currentUser, RideDto rideDto) {
        User user = userService.getUser(currentUser);
        Ride ride = new Ride();
        ride.setDriver(user);
        ride.setOrigin(rideDto.getOrigin());
        ride.setDestination(rideDto.getDestination());
        ride.setDateTime(rideDto.getDateTime());
        ride.setPrice(rideDto.getPrice());
        ride.setSeatsAvailable(rideDto.getSeatsAvailable());
        ride.setSeatsTaken(0);
        ride.setStatus(RideStatus.ACTIVE);
        Ride savedRide = rideRepository.save(ride);
        try {
            auditClient.createEntry(new CreateAuditEntryDTO(
                    user.getId(),
                    "CREATE",
                    "RIDE",
                    savedRide.getId(),
                    "Ride created"));
        } catch (Exception e) {
            System.err.println("Failed to create audit entry: " + e.getMessage());
        }
        return savedRide;
    }

    @CacheEvict(value = { "rides", "ride" }, allEntries = true)
    public Ride updateRide(CustomUserDetails currentUser, UUID id, RideDto rideDto) {
        User user = userService.getUser(currentUser);
        Ride ride = getRideById(id);
        if (!ride.getDriver().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to update this ride");
        }
        ride.setOrigin(rideDto.getOrigin());
        ride.setDestination(rideDto.getDestination());
        ride.setDateTime(rideDto.getDateTime());
        ride.setPrice(rideDto.getPrice());
        ride.setSeatsAvailable(rideDto.getSeatsAvailable());
        Ride savedRide = rideRepository.save(ride);
        try {
            auditClient.createEntry(new CreateAuditEntryDTO(
                    user.getId(),
                    "UPDATE",
                    "RIDE",
                    savedRide.getId(),
                    "Ride updated"));
        } catch (Exception e) {
            System.err.println("Failed to create audit entry: " + e.getMessage());
        }
        return savedRide;
    }

    @CacheEvict(value = { "rides", "ride" }, allEntries = true)
    public void deleteRide(CustomUserDetails currentUser, UUID id) {
        User user = userService.getUser(currentUser);
        Ride ride = getRideById(id);
        if (!ride.getDriver().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to delete this ride");
        }
        rideRepository.delete(ride);
        try {
            auditClient.createEntry(new CreateAuditEntryDTO(
                    user.getId(),
                    "DELETE",
                    "RIDE",
                    id,
                    "Ride deleted"));
        } catch (Exception e) {
            System.err.println("Failed to create audit entry: " + e.getMessage());
        }
    }

    @CacheEvict(value = { "rides", "ride" }, allEntries = true)
    public Ride bookRide(CustomUserDetails currentUser, UUID id) {
        User user = userService.getUser(currentUser);
        Ride ride = getRideById(id);

        if (ride.getSeatsTaken() >= ride.getSeatsAvailable()) {
            throw new ConflictException("No seats available");
        }

        if (ride.getBookings().stream().anyMatch(booking -> booking.getPassenger().getId().equals(user.getId()) &&
                (booking.getStatus() == BookingStatus.PENDING || booking.getStatus() == BookingStatus.APPROVED))) {
            throw new ConflictException("Already booked");
        }

        Booking booking = new Booking();
        booking.setPassenger(user);
        booking.setRide(ride);
        booking.setStatus(BookingStatus.PENDING);
        bookingRepository.save(booking);

        ride.setSeatsTaken(ride.getSeatsTaken() + 1);
        if (ride.getSeatsTaken() >= ride.getSeatsAvailable()) {
            ride.setStatus(RideStatus.BOOKED);
        }
        Ride savedRide = rideRepository.save(ride);
        try {
            auditClient.createEntry(new CreateAuditEntryDTO(
                    user.getId(),
                    "BOOK",
                    "RIDE",
                    savedRide.getId(),
                    "Ride booked"));
        } catch (Exception e) {
            System.err.println("Failed to create audit entry: " + e.getMessage());
        }
        return savedRide;
    }
}
