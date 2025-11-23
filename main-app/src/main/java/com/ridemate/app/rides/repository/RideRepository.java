package com.ridemate.app.rides.repository;

import com.ridemate.app.rides.RideStatus;
import com.ridemate.app.rides.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface RideRepository extends JpaRepository<Ride, UUID> {
    List<Ride> findByStatusAndDateTimeAfter(RideStatus status, LocalDateTime dateTime);

    List<Ride> findAllByDateTimeBeforeAndStatusNot(LocalDateTime dateTime, RideStatus status);
}
