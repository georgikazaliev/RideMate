package com.ridemate.app.bookings.service;

import com.ridemate.app.bookings.BookingStatus;
import com.ridemate.app.bookings.dto.BookingDto;
import com.ridemate.app.bookings.entity.Booking;
import com.ridemate.app.bookings.events.BookingCreatedEvent;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @CacheEvict(value = "myBookings", key = "#currentUser.username")
    public Booking createBooking(CustomUserDetails currentUser, BookingDto bookingDto) {
        User user = userService.getUser(currentUser);
        Ride ride = rideRepository.findById(bookingDto.getRideId()).orElseThrow(() -> new ResourceNotFoundException("Ride not found"));
        if (ride.getSeatsTaken() >= ride.getSeatsAvailable()) {
            throw new ConflictException("No seats available");
        }
        Booking booking = new Booking();
        booking.setPassenger(user);
        booking.setRide(ride);
        booking.setStatus(BookingStatus.PENDING);
        ride.setSeatsTaken(ride.getSeatsTaken() + 1);
        rideRepository.save(ride);
        bookingRepository.save(booking);
        eventPublisher.publishEvent(new BookingCreatedEvent(this, booking));
        return booking;
    }

    @Cacheable(value = "myBookings", key = "#currentUser.username")
    public List<Booking> getMyBookings(CustomUserDetails currentUser) {
        User user = userService.getUser(currentUser);
        return bookingRepository.findByPassengerId(user.getId());
    }

    @CacheEvict(value = "myBookings", key = "#currentUser.username")
    public Booking cancelBooking(CustomUserDetails currentUser, Long id) {
        User user = userService.getUser(currentUser);
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!booking.getPassenger().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to cancel this booking");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        Ride ride = booking.getRide();
        ride.setSeatsTaken(ride.getSeatsTaken() - 1);
        rideRepository.save(ride);
        return bookingRepository.save(booking);
    }

    @CacheEvict(value = "myBookings", key = "#currentUser.username")
    public Booking rejectBooking(CustomUserDetails currentUser, Long id) {
        User user = userService.getUser(currentUser);
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!booking.getRide().getDriver().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to reject this booking");
        }
        booking.setStatus(BookingStatus.REJECTED);
        Ride ride = booking.getRide();
        ride.setSeatsTaken(ride.getSeatsTaken() - 1);
        rideRepository.save(ride);
        return bookingRepository.save(booking);
    }

    @CacheEvict(value = "myBookings", key = "#currentUser.username")
    public Booking approveBooking(CustomUserDetails currentUser, Long id) {
        User user = userService.getUser(currentUser);
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!booking.getRide().getDriver().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to approve this booking");
        }
        booking.setStatus(BookingStatus.APPROVED);
        return bookingRepository.save(booking);
    }
}
