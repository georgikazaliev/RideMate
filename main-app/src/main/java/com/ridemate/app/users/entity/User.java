package com.ridemate.app.users.entity;

import com.ridemate.app.bookings.entity.Booking;
import com.ridemate.app.rides.entity.Ride;
import com.ridemate.app.users.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CurrentTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private Double rating;
    private String profileImageUrl;

    @CreatedDate
    private Instant createdAt;

    @OneToMany(mappedBy = "driver")
    private List<Ride> rides = new ArrayList<>();

    @OneToMany(mappedBy = "passenger")
    private List<Booking> bookings = new ArrayList<>();

}
