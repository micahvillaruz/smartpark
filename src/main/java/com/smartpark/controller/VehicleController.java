package com.smartpark.controller;

import com.smartpark.dto.VehicleRequest;
import com.smartpark.dto.VehicleResponse;
import com.smartpark.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    public ResponseEntity<VehicleResponse> register(@Valid @RequestBody VehicleRequest request) {
        VehicleResponse response = vehicleService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{licensePlate}")
    public ResponseEntity<VehicleResponse> getById(@PathVariable String licensePlate) {
        return ResponseEntity.ok(vehicleService.getById(licensePlate));
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getAll() {
        return ResponseEntity.ok(vehicleService.getAll());
    }
}