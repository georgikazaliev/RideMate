package com.ridemate.app.rides.entity;

import com.ridemate.app.bookings.entity.Booking;
import com.ridemate.app.rides.RideStatus;
import com.ridemate.app.users.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rides")
@Data
public class Ride {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private User driver;

    private String origin;
    private String destination;

    private Instant dateTime;

    private Double price;
    private Integer seatsAvailable;
    private Integer seatsTaken;

    @Enumerated(EnumType.STRING)
    private RideStatus status;

    @OneToMany(mappedBy = "ride")
    private List<Booking> bookings = new ArrayList<>();
}
