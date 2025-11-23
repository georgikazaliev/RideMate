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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RideService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingRepository bookingRepository;


    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }


    public Ride getRideById(Long id) {
        return rideRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ride not found"));
    }


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
        return rideRepository.save(ride);
    }


    public Ride updateRide(CustomUserDetails currentUser, Long id, RideDto rideDto) {
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
        return rideRepository.save(ride);
    }

    public void deleteRide(CustomUserDetails currentUser, Long id) {
        User user = userService.getUser(currentUser);
        Ride ride = getRideById(id);
        if (!ride.getDriver().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to delete this ride");
        }
        rideRepository.delete(ride);
    }


    public Ride bookRide(CustomUserDetails currentUser, Long id) {
        User user = userService.getUser(currentUser);
        Ride ride = getRideById(id);

        if (ride.getSeatsTaken() >= ride.getSeatsAvailable()) {
            throw new ConflictException("No seats available");
        }

        if (ride.getBookings().stream().anyMatch(booking -> booking.getPassenger().getId().equals(user.getId()))) {
            throw new ConflictException("Already booked");
        }

        Booking booking = new Booking();
        booking.setPassenger(user);
        booking.setRide(ride);
        booking.setStatus(BookingStatus.PENDING);
        bookingRepository.save(booking);

        ride.setSeatsTaken(ride.getSeatsTaken() + 1);
        return rideRepository.save(ride);
    }
}
