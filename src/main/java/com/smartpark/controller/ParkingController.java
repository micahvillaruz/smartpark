package com.smartpark.controller;

import com.smartpark.dto.CheckInRequest;
import com.smartpark.dto.CheckOutRequest;
import com.smartpark.dto.ParkingSessionResponse;
import com.smartpark.service.ParkingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking")
public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @PostMapping("/check-in")
    public ResponseEntity<ParkingSessionResponse> checkIn(@Valid @RequestBody CheckInRequest request) {
        return ResponseEntity.ok(parkingService.checkIn(request));
    }

    @PostMapping("/check-out")
    public ResponseEntity<ParkingSessionResponse> checkOut(@Valid @RequestBody CheckOutRequest request) {
        return ResponseEntity.ok(parkingService.checkOut(request));
    }

    @GetMapping("/lots/{lotId}/vehicles")
    public ResponseEntity<List<ParkingSessionResponse>> getParkedVehicles(@PathVariable String lotId) {
        return ResponseEntity.ok(parkingService.getActiveSessionsByLot(lotId));
    }
}