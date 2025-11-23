package com.ridemate.app.vehicles.service;

import com.ridemate.app.security.CustomUserDetails;
import com.ridemate.app.users.entity.User;
import com.ridemate.app.exceptions.AccessDeniedException;
import com.ridemate.app.exceptions.ResourceNotFoundException;
import com.ridemate.app.users.service.UserService;
import com.ridemate.app.vehicles.dto.VehicleDto;
import com.ridemate.app.vehicles.entity.Vehicle;
import com.ridemate.app.vehicles.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService{

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserService userService;

    public List<Vehicle> getMyVehicles(CustomUserDetails currentUser) {
        User user = userService.getUser(currentUser);
        return vehicleRepository.findByDriverId(user.getId());
    }

    public Vehicle createVehicle(CustomUserDetails currentUser, VehicleDto vehicleDto) {
        User user = userService.getUser(currentUser);
        Vehicle vehicle = new Vehicle();
        vehicle.setDriver(user);
        vehicle.setMake(vehicleDto.getMake());
        vehicle.setModel(vehicleDto.getModel());
        vehicle.setSeats(vehicleDto.getSeats());
        vehicle.setImageUrl(vehicleDto.getImageUrl());
        return vehicleRepository.save(vehicle);
    }

    public Vehicle updateVehicle(CustomUserDetails currentUser, Long id, VehicleDto vehicleDto) {
        User user = userService.getUser(currentUser);
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        if (!vehicle.getDriver().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to update this vehicle");
        }
        vehicle.setMake(vehicleDto.getMake());
        vehicle.setModel(vehicleDto.getModel());
        vehicle.setSeats(vehicleDto.getSeats());
        vehicle.setImageUrl(vehicleDto.getImageUrl());
        return vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(CustomUserDetails currentUser, Long id) {
        User user = userService.getUser(currentUser);
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        if (!vehicle.getDriver().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to delete this vehicle");
        }
        vehicleRepository.delete(vehicle);
    }
}
