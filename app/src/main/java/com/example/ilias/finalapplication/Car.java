package com.example.ilias.finalapplication;

public class Car {

    private String id;
    private String licenseplate;
    private String kind;
    private String length;

    public Car(String id, String licenseplate, String kind, String length) {
        this.id = id;
        this.licenseplate = licenseplate;
        this.kind = kind;
        this.length = length;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id='" + id + '\'' +
                ", licenseplate='" + licenseplate + '\'' +
                ", kind='" + kind + '\'' +
                ", length='" + length + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLicenseplate() {
        return licenseplate;
    }

    public void setLicenseplate(String licenseplate) {
        this.licenseplate = licenseplate;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }
}
