package com.ridemate.app.bookings.controller;

import com.ridemate.app.bookings.dto.BookingDto;
import com.ridemate.app.bookings.dto.BookingResponse;
import com.ridemate.app.bookings.service.BookingService;
import com.ridemate.app.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody BookingDto bookingDto) {
        return ResponseEntity.ok(new BookingResponse(bookingService.createBooking(currentUser, bookingDto)));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<BookingResponse>> getMine(@AuthenticationPrincipal CustomUserDetails currentUser) {
        List<BookingResponse> bookings = bookingService.getMyBookings(currentUser).stream()
                .map(BookingResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/requests")
    public ResponseEntity<List<BookingResponse>> getRequests(@AuthenticationPrincipal CustomUserDetails currentUser) {
        List<BookingResponse> bookings = bookingService.getBookingRequests(currentUser).stream()
                .map(BookingResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookings);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id) {
        return ResponseEntity.ok(new BookingResponse(bookingService.cancelBooking(currentUser, id)));
    }

    @PutMapping("{id}/reject")
    public ResponseEntity<BookingResponse> rejectBooking(@AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id) {
        return ResponseEntity.ok(new BookingResponse(bookingService.rejectBooking(currentUser, id)));
    }

    @PutMapping("{id}/approve")
    public ResponseEntity<BookingResponse> approveBooking(@AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id) {
        return ResponseEntity.ok(new BookingResponse(bookingService.approveBooking(currentUser, id)));
    }

}
