package com.ridemate.app.rides.dto;

import com.ridemate.app.rides.RideStatus;
import com.ridemate.app.rides.entity.Ride;
import com.ridemate.app.users.dto.responses.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideResponse {
    private UUID id;
    private UserResponse driver;
    private String origin;
    private String destination;
    private LocalDateTime dateTime;
    private Double price;
    private Integer seatsAvailable;
    private Integer seatsTaken;
    private RideStatus status;
    private UUID driverId;

    public RideResponse(Ride ride) {
        this.id = ride.getId();
        this.driver = new UserResponse(ride.getDriver());
        this.origin = ride.getOrigin();
        this.destination = ride.getDestination();
        this.dateTime = ride.getDateTime();
        this.price = ride.getPrice();
        this.seatsAvailable = ride.getSeatsAvailable();
        this.seatsTaken = ride.getSeatsTaken();
        this.status = ride.getStatus();
        this.driverId = ride.getDriver().getId();
    }
}
