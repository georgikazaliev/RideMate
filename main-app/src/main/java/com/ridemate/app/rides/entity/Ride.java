package com.ridemate.app.rides.entity;

import com.ridemate.app.bookings.entity.Booking;
import com.ridemate.app.rides.RideStatus;
import com.ridemate.app.users.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rides")
@Data
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private User driver;

    private String origin;
    private String destination;

    private LocalDateTime dateTime;

    private Double price;
    private Integer seatsAvailable;
    private Integer seatsTaken;

    @Enumerated(EnumType.STRING)
    private RideStatus status;

    @OneToMany(mappedBy = "ride")
    @lombok.ToString.Exclude
    private List<Booking> bookings = new ArrayList<>();
}
