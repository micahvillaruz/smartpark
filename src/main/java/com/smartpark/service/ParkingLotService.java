package com.smartpark.service;

import com.smartpark.dto.ParkingLotRequest;
import com.smartpark.dto.ParkingLotResponse;
import com.smartpark.entity.ParkingLot;
import com.smartpark.repository.ParkingLotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;

    public ParkingLotService(ParkingLotRepository parkingLotRepository) {
        this.parkingLotRepository = parkingLotRepository;
    }

    @Transactional
    public ParkingLotResponse register(ParkingLotRequest request) {
        if (parkingLotRepository.existsById(request.getLotId())) {
            throw new IllegalArgumentException("Parking lot with ID '" + request.getLotId() + "' already exists");
        }

        ParkingLot lot = new ParkingLot(
                request.getLotId(),
                request.getLocation(),
                request.getCapacity(),
                0,
                request.getCostPerMinute()
        );

        ParkingLot saved = parkingLotRepository.save(lot);
        return ParkingLotResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public ParkingLotResponse getById(String lotId) {
        ParkingLot lot = parkingLotRepository.findById(lotId)
                .orElseThrow(() -> new IllegalArgumentException("Parking lot not found: " + lotId));
        return ParkingLotResponse.from(lot);
    }

    @Transactional(readOnly = true)
    public List<ParkingLotResponse> getAll() {
        return parkingLotRepository.findAll().stream()
                .map(ParkingLotResponse::from)
                .toList();
    }
}