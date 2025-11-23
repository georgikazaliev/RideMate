package com.ridemate.app.bookings.dto;

import com.ridemate.app.bookings.BookingStatus;
import com.ridemate.app.bookings.entity.Booking;
import com.ridemate.app.rides.dto.RideResponse;
import com.ridemate.app.users.dto.responses.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    private Long id;
    private RideResponse ride;
    private UserResponse passenger;
    private BookingStatus status;

    public BookingResponse(Booking booking) {
        this.id = booking.getId();
        this.ride = new RideResponse(booking.getRide());
        this.passenger = new UserResponse(booking.getPassenger());
        this.status = booking.getStatus();
    }
}
