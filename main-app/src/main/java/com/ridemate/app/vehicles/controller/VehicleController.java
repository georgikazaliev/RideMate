package com.ridemate.app.vehicles.controller;

import com.ridemate.app.security.CustomUserDetails;
import com.ridemate.app.vehicles.dto.VehicleDto;
import com.ridemate.app.vehicles.dto.VehicleResponse;
import com.ridemate.app.vehicles.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @GetMapping("/mine")
    public ResponseEntity<List<VehicleResponse>> getMine(@AuthenticationPrincipal CustomUserDetails currentUser){
        List<VehicleResponse> vehicles = vehicleService.getMyVehicles(currentUser).stream()
                .map(VehicleResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(vehicles);
    }

    @PostMapping("/")
    public ResponseEntity<VehicleResponse> create(@AuthenticationPrincipal CustomUserDetails currentUser, @Valid @RequestBody VehicleDto vehicleDto){
        return ResponseEntity.ok(new VehicleResponse(vehicleService.createVehicle(currentUser, vehicleDto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> update(@AuthenticationPrincipal CustomUserDetails currentUser, @PathVariable Long id, @Valid @RequestBody VehicleDto vehicleDto){
        return ResponseEntity.ok(new VehicleResponse(vehicleService.updateVehicle(currentUser, id, vehicleDto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal CustomUserDetails currentUser, @PathVariable Long id){
        vehicleService.deleteVehicle(currentUser, id);
        return ResponseEntity.noContent().build();
    }
}
