package com.ridemate.app.rides.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideDto {
    private UUID id;
    private UUID driverId;

    @NotBlank(message = "Origin is mandatory")
    private String origin;

    @NotBlank(message = "Destination is mandatory")
    private String destination;

    @NotNull(message = "Date time is mandatory")
    @Future(message = "Date time must be in the future")
    private LocalDateTime dateTime;

    @NotNull(message = "Price is mandatory")
    @Positive(message = "Price must be positive")
    private Double price;

    @NotNull(message = "Seats available is mandatory")
    @Min(value = 1, message = "Seats available must be at least 1")
    private Integer seatsAvailable;
}
