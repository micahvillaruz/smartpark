package com.smartpark.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class ParkingLotRequest {

    @NotBlank(message = "Lot ID is required")
    @Size(max = 50, message = "Lot ID must be at most 50 characters")
    private String lotId;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @NotNull(message = "Cost per minute is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Cost per minute must be greater than 0")
    private BigDecimal costPerMinute;

    public ParkingLotRequest() {}

    public String getLotId() { return lotId; }
    public void setLotId(String lotId) { this.lotId = lotId; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public BigDecimal getCostPerMinute() { return costPerMinute; }
    public void setCostPerMinute(BigDecimal costPerMinute) { this.costPerMinute = costPerMinute; }
}