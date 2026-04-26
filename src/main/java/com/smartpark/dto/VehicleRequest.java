package com.smartpark.dto;

import com.smartpark.entity.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class VehicleRequest {

    @NotBlank(message = "License plate is required")
    @Pattern(regexp = "^[A-Za-z0-9-]+$",
            message = "License plate can only contain letters, numbers, and dashes")
    private String licensePlate;

    @NotNull(message = "Vehicle type is required")
    private VehicleType type;

    @NotBlank(message = "Owner name is required")
    @Pattern(regexp = "^[A-Za-z ]+$",
            message = "Owner name can only contain letters and spaces")
    private String ownerName;

    public VehicleRequest() {}

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public VehicleType getType() { return type; }
    public void setType(VehicleType type) { this.type = type; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
}