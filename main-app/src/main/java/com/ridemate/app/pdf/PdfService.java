package com.ridemate.app.pdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.ridemate.app.bookings.entity.Booking;
import com.ridemate.app.bookings.repository.BookingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

    @Autowired
    private BookingRepository bookingRepository;

    public ByteArrayInputStream createPdf(java.util.UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Booking Confirmation"));
        document.add(new Paragraph("Booking ID: " + booking.getId()));
        document.add(new Paragraph("Booking Date: " + booking.getCreatedAt()));
        document.add(new Paragraph("Booking Status: " + booking.getStatus()));
        document.add(new Paragraph("Booking Price: " + booking.getRide().getPrice()));
        document.add(new Paragraph("Booking Driver: " + booking.getRide().getDriver().getUsername()));
        document.add(new Paragraph("Thank you for choosing RideMate!"));

        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }
}
