package com.ridemate.app.bookings.events;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class BookingCreatedListener implements ApplicationListener<BookingCreatedEvent> {

    @Override
    public void onApplicationEvent(BookingCreatedEvent event) {
        // todo
        System.out.println("Booking created: " + event.getBooking());
    }
}
