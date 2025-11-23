package com.ridemate.app.bookings.dto;

import com.ridemate.app.bookings.BookingStatus;
import com.ridemate.app.bookings.entity.Booking;
import com.ridemate.app.rides.dto.RideResponse;
import com.ridemate.app.users.dto.responses.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    private UUID id;
    private UUID rideId;
    private UUID passengerId;
    private BookingStatus status;
    private RideResponse ride;
    private UserResponse passenger;

    public BookingResponse(Booking booking) {
        this.id = booking.getId();
        this.rideId = booking.getRide().getId();
        this.passengerId = booking.getPassenger().getId();
        this.status = booking.getStatus();
        this.ride = new RideResponse(booking.getRide());
        this.passenger = new UserResponse(booking.getPassenger());
    }
}
