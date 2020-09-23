package com.example.passengerapp;

public class DriverData {
    String id;
    double latitude;
    double longitude;
    double speed;
    String busNo;
//    String location;
//    String busNo;
    int status;

    public DriverData() {}

    public DriverData(String id, double latitude, double longitude, int status, String busNo,double speed) {
        this.id = id;
        this.speed=speed;
        this.latitude = latitude;
        this.longitude = longitude;
        this.busNo=busNo;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getBusNo() {
        return busNo;
    }

    public void setBusNo(String busNo) {
        this.busNo = busNo;
    }
}
