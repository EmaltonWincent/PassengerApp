package com.example.passengerapp;

public class Customer {

    int id;
    String pName;
    double latitude;
    double longitude;
    String location;
    String busNo;
    int status;

    public Customer() {}

    public Customer(int id, String pName, double latitude, double longitude, String location, String busNo, int status) {
        this.id = id;
        this.pName = pName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.busNo = busNo;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public void setId(int id) {
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBusNo() {
        return busNo;
    }

    public void setBusNo(String busNo) {
        this.busNo = busNo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
