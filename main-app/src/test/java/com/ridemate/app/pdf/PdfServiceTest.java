package com.ridemate.app.pdf;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ridemate.app.bookings.BookingStatus;
import com.ridemate.app.bookings.entity.Booking;
import com.ridemate.app.bookings.repository.BookingRepository;
import com.ridemate.app.rides.entity.Ride;
import com.ridemate.app.users.entity.User;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdfServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private PdfService pdfService;

    @Test
    void createPdf_ShouldReturnPdfStream() throws Exception {
        UUID bookingId = UUID.randomUUID();
        User driver = new User();
        driver.setUsername("test-driver");
        Ride ride = new Ride();
        ride.setPrice(25.50);
        ride.setDriver(driver);
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setRide(ride);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        ByteArrayInputStream result = pdfService.createPdf(bookingId);
        assertNotNull(result);
        assertTrue(result.available() > 0);
        byte[] header = new byte[4];
        int read = result.read(header);
        assertEquals(4, read);
        String pdfHeader = new String(header);
        assertEquals("%PDF", pdfHeader);
    }
}
