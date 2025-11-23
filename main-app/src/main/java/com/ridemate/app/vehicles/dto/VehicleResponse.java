package com.ridemate.app.vehicles.dto;

import com.ridemate.app.users.dto.responses.UserResponse;
import com.ridemate.app.vehicles.entity.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleResponse {
    private Long id;
    private UserResponse driver;
    private String make;
    private String model;
    private Integer seats;
    private String imageUrl;

    public VehicleResponse(Vehicle vehicle) {
        this.id = vehicle.getId();
        this.driver = new UserResponse(vehicle.getDriver());
        this.make = vehicle.getMake();
        this.model = vehicle.getModel();
        this.seats = vehicle.getSeats();
        this.imageUrl = vehicle.getImageUrl();
    }
}
