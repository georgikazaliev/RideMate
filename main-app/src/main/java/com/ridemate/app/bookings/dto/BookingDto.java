package com.ridemate.app.bookings.dto;

import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private UUID id;
    private UUID rideId;
    private UUID passengerId;
}
