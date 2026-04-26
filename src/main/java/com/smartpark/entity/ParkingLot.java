package com.smartpark.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "parking_lot")
public class ParkingLot {

    @Id
    @Column(length = 50)
    private String lotId;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Integer occupiedSpaces = 0;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal costPerMinute;

    public ParkingLot() {
    }

    public ParkingLot(String lotId, String location, Integer capacity,
                      Integer occupiedSpaces, BigDecimal costPerMinute) {
        this.lotId = lotId;
        this.location = location;
        this.capacity = capacity;
        this.occupiedSpaces = occupiedSpaces;
        this.costPerMinute = costPerMinute;
    }

    public String getLotId() { return lotId; }
    public void setLotId(String lotId) { this.lotId = lotId; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Integer getOccupiedSpaces() { return occupiedSpaces; }
    public void setOccupiedSpaces(Integer occupiedSpaces) { this.occupiedSpaces = occupiedSpaces; }

    public BigDecimal getCostPerMinute() { return costPerMinute; }
    public void setCostPerMinute(BigDecimal costPerMinute) { this.costPerMinute = costPerMinute; }

    public int getAvailableSpaces() {
        return capacity - occupiedSpaces;
    }

    public boolean isFull() {
        return occupiedSpaces >= capacity;
    }
}