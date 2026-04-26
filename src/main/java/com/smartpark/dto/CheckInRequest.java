package com.smartpark.dto;

import jakarta.validation.constraints.NotBlank;

public class CheckInRequest {

    @NotBlank(message = "License plate is required")
    private String licensePlate;

    @NotBlank(message = "Lot ID is required")
    private String lotId;

    public CheckInRequest() {}

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getLotId() { return lotId; }
    public void setLotId(String lotId) { this.lotId = lotId; }
}