package com.smartpark.service;

import com.smartpark.dto.VehicleRequest;
import com.smartpark.dto.VehicleResponse;
import com.smartpark.entity.Vehicle;
import com.smartpark.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional
    public VehicleResponse register(VehicleRequest request) {
        if (vehicleRepository.existsById(request.getLicensePlate())) {
            throw new IllegalArgumentException(
                    "Vehicle with license plate '" + request.getLicensePlate() + "' already exists");
        }

        Vehicle vehicle = new Vehicle(
                request.getLicensePlate(),
                request.getType(),
                request.getOwnerName()
        );

        Vehicle saved = vehicleRepository.save(vehicle);
        return VehicleResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public VehicleResponse getById(String licensePlate) {
        Vehicle vehicle = vehicleRepository.findById(licensePlate)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + licensePlate));
        return VehicleResponse.from(vehicle);
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> getAll() {
        return vehicleRepository.findAll().stream()
                .map(VehicleResponse::from)
                .toList();
    }
}