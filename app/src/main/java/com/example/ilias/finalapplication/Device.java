package com.example.ilias.finalapplication;

public class Device {

    private int ID;
    private double longitude;
    private double latitude;
    private int Isbezet;
    private int lengte;

    public Device(int ID, double longitude, double latitude, int isbezet, int lengte) {
        this.ID = ID;
        this.longitude = longitude;
        this.latitude = latitude;
        Isbezet = isbezet;
        this.lengte = lengte;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int isIsbezet() {
        return Isbezet;
    }

    public void setIsbezet(int isbezet) {
        Isbezet = isbezet;
    }

    public int getLengte() {
        return lengte;
    }

    public void setLengte(int lengte) {
        this.lengte = lengte;
    }
}
