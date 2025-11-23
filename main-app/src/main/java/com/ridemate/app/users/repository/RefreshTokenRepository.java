package com.ridemate.app.users.repository;

import com.ridemate.app.users.entity.RefreshToken;
import com.ridemate.app.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
