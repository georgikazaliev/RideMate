package com.ridemate.app.rides.scheduler;

import com.ridemate.app.rides.RideStatus;
import com.ridemate.app.rides.entity.Ride;
import com.ridemate.app.rides.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RideScheduler {

    @Autowired
    private RideRepository rideRepository;

    @Scheduled(fixedRate = 60000)
    public void updateRideStatuses() {
        List<Ride> ridesToUpdate = rideRepository.findAllByDateTimeBeforeAndStatusNot(LocalDateTime.now(),
                RideStatus.COMPLETED);
        for (Ride ride : ridesToUpdate) {
            ride.setStatus(RideStatus.COMPLETED);
            rideRepository.save(ride);
        }
    }
}
