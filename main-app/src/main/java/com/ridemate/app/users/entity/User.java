package com.ridemate.app.users.entity;

import com.ridemate.app.bookings.entity.Booking;
import com.ridemate.app.rides.entity.Ride;
import com.ridemate.app.users.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {
    public User(UUID userId, String username, String email, String password) {
        this.id = userId;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "driver")
    @lombok.ToString.Exclude
    private List<Ride> rides = new ArrayList<>();

    @OneToMany(mappedBy = "passenger")
    @lombok.ToString.Exclude
    private List<Booking> bookings = new ArrayList<>();

}
