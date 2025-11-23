package com.ridemate.app.pdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

    public ByteArrayInputStream createPdf(java.util.UUID bookingId) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Booking Confirmation"));
        document.add(new Paragraph("Booking ID: " + bookingId));
        document.add(new Paragraph("Thank you for choosing RideMate!"));

        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }
}
