package com.smartpark.controller;

import com.smartpark.dto.ParkingLotRequest;
import com.smartpark.dto.ParkingLotResponse;
import com.smartpark.service.ParkingLotService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lots")
public class ParkingLotController {

    private final ParkingLotService parkingLotService;

    public ParkingLotController(ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
    }

    @PostMapping
    public ResponseEntity<ParkingLotResponse> register(@Valid @RequestBody ParkingLotRequest request) {
        ParkingLotResponse response = parkingLotService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{lotId}")
    public ResponseEntity<ParkingLotResponse> getById(@PathVariable String lotId) {
        return ResponseEntity.ok(parkingLotService.getById(lotId));
    }

    @GetMapping
    public ResponseEntity<List<ParkingLotResponse>> getAll() {
        return ResponseEntity.ok(parkingLotService.getAll());
    }
}