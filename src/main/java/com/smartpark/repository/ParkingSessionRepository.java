package com.smartpark.repository;

import com.smartpark.entity.ParkingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Long> {

    Optional<ParkingSession> findByVehicleLicensePlateAndActiveTrue(String licensePlate);

    List<ParkingSession> findByParkingLotLotIdAndActiveTrue(String lotId);

    List<ParkingSession> findByActiveTrue();

    @Query("SELECT s FROM ParkingSession s WHERE s.active = true AND s.checkInTime < :threshold")
    List<ParkingSession> findActiveSessionsOlderThan(@Param("threshold") LocalDateTime threshold);
}