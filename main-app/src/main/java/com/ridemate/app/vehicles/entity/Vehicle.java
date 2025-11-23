package com.ridemate.app.vehicles.entity;

import com.ridemate.app.users.entity.User;
import jakarta.persistence.*;
import lombok.Data;

@Table(name="vehicles")
@Entity
@Data
public class Vehicle {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private User driver;

    private String make;
    private String model;
    private Integer seats;
    private String imageUrl;
}
