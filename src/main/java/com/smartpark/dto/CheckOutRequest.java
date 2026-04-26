package com.smartpark.dto;

import jakarta.validation.constraints.NotBlank;

public class CheckOutRequest {

    @NotBlank(message = "License plate is required")
    private String licensePlate;

    public CheckOutRequest() {}

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
}