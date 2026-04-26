package com.smartpark.service;

import com.smartpark.dto.CheckInRequest;
import com.smartpark.dto.CheckOutRequest;
import com.smartpark.dto.ParkingSessionResponse;
import com.smartpark.entity.ParkingLot;
import com.smartpark.entity.ParkingSession;
import com.smartpark.entity.Vehicle;
import com.smartpark.repository.ParkingLotRepository;
import com.smartpark.repository.ParkingSessionRepository;
import com.smartpark.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParkingService {

    private final ParkingLotRepository parkingLotRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingSessionRepository parkingSessionRepository;

    public ParkingService(ParkingLotRepository parkingLotRepository,
                          VehicleRepository vehicleRepository,
                          ParkingSessionRepository parkingSessionRepository) {
        this.parkingLotRepository = parkingLotRepository;
        this.vehicleRepository = vehicleRepository;
        this.parkingSessionRepository = parkingSessionRepository;
    }

    @Transactional
    public ParkingSessionResponse checkIn(CheckInRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.getLicensePlate())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Vehicle not found: " + request.getLicensePlate()));

        ParkingLot lot = parkingLotRepository.findById(request.getLotId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Parking lot not found: " + request.getLotId()));

        // Rule: a vehicle can only be parked in one lot at a time
        parkingSessionRepository.findByVehicleLicensePlateAndActiveTrue(request.getLicensePlate())
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "Vehicle " + request.getLicensePlate()
                                    + " is already parked in lot " + existing.getParkingLot().getLotId());
                });

        // Rule: prevent parking in a full lot
        if (lot.isFull()) {
            throw new IllegalStateException("Parking lot " + lot.getLotId() + " is full");
        }

        ParkingSession session = new ParkingSession(vehicle, lot, LocalDateTime.now());
        lot.setOccupiedSpaces(lot.getOccupiedSpaces() + 1);

        ParkingSession saved = parkingSessionRepository.save(session);
        parkingLotRepository.save(lot);

        return ParkingSessionResponse.from(saved);
    }

    @Transactional
    public ParkingSessionResponse checkOut(CheckOutRequest request) {
        ParkingSession session = parkingSessionRepository
                .findByVehicleLicensePlateAndActiveTrue(request.getLicensePlate())
                .orElseThrow(() -> new IllegalStateException(
                        "No active parking session found for vehicle: " + request.getLicensePlate()));

        LocalDateTime checkOutTime = LocalDateTime.now();
        BigDecimal cost = calculateCost(session.getCheckInTime(), checkOutTime,
                session.getParkingLot().getCostPerMinute());

        session.setCheckOutTime(checkOutTime);
        session.setCost(cost);
        session.setActive(false);

        ParkingLot lot = session.getParkingLot();
        lot.setOccupiedSpaces(Math.max(0, lot.getOccupiedSpaces() - 1));

        parkingSessionRepository.save(session);
        parkingLotRepository.save(lot);

        return ParkingSessionResponse.from(session);
    }

    @Transactional
    public ParkingSessionResponse autoCheckOut(ParkingSession session) {
        LocalDateTime checkOutTime = LocalDateTime.now();
        BigDecimal cost = calculateCost(session.getCheckInTime(), checkOutTime,
                session.getParkingLot().getCostPerMinute());

        session.setCheckOutTime(checkOutTime);
        session.setCost(cost);
        session.setActive(false);
        session.setAutoCheckedOut(true);

        ParkingLot lot = session.getParkingLot();
        lot.setOccupiedSpaces(Math.max(0, lot.getOccupiedSpaces() - 1));

        parkingSessionRepository.save(session);
        parkingLotRepository.save(lot);

        return ParkingSessionResponse.from(session);
    }

    @Transactional(readOnly = true)
    public List<ParkingSessionResponse> getActiveSessionsByLot(String lotId) {
        if (!parkingLotRepository.existsById(lotId)) {
            throw new IllegalArgumentException("Parking lot not found: " + lotId);
        }

        return parkingSessionRepository.findByParkingLotLotIdAndActiveTrue(lotId).stream()
                .map(ParkingSessionResponse::from)
                .toList();
    }

    /**
     * Calculate cost based on minutes parked.
     * Minutes are rounded UP (any fraction of a minute counts as a full minute) so
     * very short stays still incur a charge.
     */
    BigDecimal calculateCost(LocalDateTime checkIn, LocalDateTime checkOut, BigDecimal costPerMinute) {
        long seconds = Duration.between(checkIn, checkOut).getSeconds();
        long minutes = (seconds + 59) / 60;
        if (minutes < 1) {
            minutes = 1;
        }
        return costPerMinute
                .multiply(BigDecimal.valueOf(minutes))
                .setScale(2, RoundingMode.HALF_UP);
    }
}