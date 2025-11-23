package com.ridemate.app.rides.controller;

import com.ridemate.app.rides.dto.RideDto;
import com.ridemate.app.rides.dto.RideResponse;
import com.ridemate.app.rides.service.RideService;
import com.ridemate.app.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/rides")
public class RideController {

    @Autowired
    private RideService rideService;

    @GetMapping("/")
    public ResponseEntity<List<RideResponse>> getRides(@AuthenticationPrincipal CustomUserDetails currentUser) {
        List<RideResponse> rides = rideService.getAllRides(currentUser).stream()
                .map(RideResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(rides);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideResponse> getRide(@PathVariable UUID id) {
        return ResponseEntity.ok(new RideResponse(rideService.getRideById(id)));
    }

    @PostMapping("/")
    public ResponseEntity<RideResponse> createRide(@AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody RideDto rideDto) {
        return ResponseEntity.ok(new RideResponse(rideService.createRide(currentUser, rideDto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RideResponse> updateRide(@AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id, @Valid @RequestBody RideDto rideDto) {
        return ResponseEntity.ok(new RideResponse(rideService.updateRide(currentUser, id, rideDto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRide(@AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id) {
        rideService.deleteRide(currentUser, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/book")
    public ResponseEntity<RideResponse> bookRide(@AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id) {
        return ResponseEntity.ok(new RideResponse(rideService.bookRide(currentUser, id)));
    }

}
