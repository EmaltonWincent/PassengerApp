package com.example.passengerapp;

public class model {
    double lat;
    double lon;

    public model(double lat,double lon){
        this.lat=lat;
        this.lon=lon;

    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
