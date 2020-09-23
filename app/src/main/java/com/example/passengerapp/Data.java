package com.example.passengerapp;

public class Data {

    String placeId;
    double speedLimit;
    String units;

    public Data() {
    }

    public Data(String placeId, double speedLimit, String units) {
        this.placeId = placeId;
        this.speedLimit = speedLimit;
        this.units = units;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public double getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(double speedLimit) {
        this.speedLimit = speedLimit;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }
}
