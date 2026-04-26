package com.smartpark.dto;

import com.smartpark.entity.ParkingLot;

import java.math.BigDecimal;

public class ParkingLotResponse {

    private String lotId;
    private String location;
    private Integer capacity;
    private Integer occupiedSpaces;
    private Integer availableSpaces;
    private BigDecimal costPerMinute;

    public ParkingLotResponse() {}

    public static ParkingLotResponse from(ParkingLot lot) {
        ParkingLotResponse r = new ParkingLotResponse();
        r.lotId = lot.getLotId();
        r.location = lot.getLocation();
        r.capacity = lot.getCapacity();
        r.occupiedSpaces = lot.getOccupiedSpaces();
        r.availableSpaces = lot.getAvailableSpaces();
        r.costPerMinute = lot.getCostPerMinute();
        return r;
    }

    public String getLotId() { return lotId; }
    public void setLotId(String lotId) { this.lotId = lotId; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Integer getOccupiedSpaces() { return occupiedSpaces; }
    public void setOccupiedSpaces(Integer occupiedSpaces) { this.occupiedSpaces = occupiedSpaces; }

    public Integer getAvailableSpaces() { return availableSpaces; }
    public void setAvailableSpaces(Integer availableSpaces) { this.availableSpaces = availableSpaces; }

    public BigDecimal getCostPerMinute() { return costPerMinute; }
    public void setCostPerMinute(BigDecimal costPerMinute) { this.costPerMinute = costPerMinute; }
}