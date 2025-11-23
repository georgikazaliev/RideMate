package com.ridemate.app.bookings.entity;

import com.ridemate.app.bookings.BookingStatus;
import com.ridemate.app.rides.entity.Ride;
import com.ridemate.app.users.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name="bookings")
@Data
public class Booking {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ride_id")
    private Ride ride;

    @ManyToOne
    @JoinColumn(name = "passenger_id")
    private User passenger;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private Instant createdAt = Instant.now();
}
