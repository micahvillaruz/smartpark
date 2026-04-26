package com.smartpark.dto;

import com.smartpark.entity.ParkingSession;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

public class ParkingSessionResponse {

    private Long sessionId;
    private String licensePlate;
    private String ownerName;
    private String vehicleType;
    private String lotId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Long durationMinutes;
    private BigDecimal cost;
    private boolean active;
    private boolean autoCheckedOut;

    public ParkingSessionResponse() {}

    public static ParkingSessionResponse from(ParkingSession s) {
        ParkingSessionResponse r = new ParkingSessionResponse();
        r.sessionId = s.getId();
        r.licensePlate = s.getVehicle().getLicensePlate();
        r.ownerName = s.getVehicle().getOwnerName();
        r.vehicleType = s.getVehicle().getType().name();
        r.lotId = s.getParkingLot().getLotId();
        r.checkInTime = s.getCheckInTime();
        r.checkOutTime = s.getCheckOutTime();
        r.cost = s.getCost();
        r.active = s.isActive();
        r.autoCheckedOut = s.isAutoCheckedOut();
        if (s.getCheckOutTime() != null) {
            r.durationMinutes = Duration.between(s.getCheckInTime(), s.getCheckOutTime()).toMinutes();
        }
        return r;
    }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getLotId() { return lotId; }
    public void setLotId(String lotId) { this.lotId = lotId; }

    public LocalDateTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalDateTime checkInTime) { this.checkInTime = checkInTime; }

    public LocalDateTime getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(LocalDateTime checkOutTime) { this.checkOutTime = checkOutTime; }

    public Long getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Long durationMinutes) { this.durationMinutes = durationMinutes; }

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isAutoCheckedOut() { return autoCheckedOut; }
    public void setAutoCheckedOut(boolean autoCheckedOut) { this.autoCheckedOut = autoCheckedOut; }
}