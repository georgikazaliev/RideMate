package com.ridemate.app.pdf;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PdfServiceTest {

    private final PdfService pdfService = new PdfService();

    @Test
    void createPdf_ShouldReturnPdfStream() throws Exception {
        UUID bookingId = UUID.randomUUID();
        ByteArrayInputStream result = pdfService.createPdf(bookingId);

        assertNotNull(result);
        assertTrue(result.available() > 0);

        byte[] header = new byte[4];
        result.read(header);
        String pdfHeader = new String(header);
        assertEquals("%PDF", pdfHeader);
    }
}
