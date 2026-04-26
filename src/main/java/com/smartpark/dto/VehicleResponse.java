package com.smartpark.dto;

import com.smartpark.entity.Vehicle;
import com.smartpark.entity.VehicleType;

public class VehicleResponse {

    private String licensePlate;
    private VehicleType type;
    private String ownerName;

    public VehicleResponse() {}

    public static VehicleResponse from(Vehicle v) {
        VehicleResponse r = new VehicleResponse();
        r.licensePlate = v.getLicensePlate();
        r.type = v.getType();
        r.ownerName = v.getOwnerName();
        return r;
    }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public VehicleType getType() { return type; }
    public void setType(VehicleType type) { this.type = type; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
}