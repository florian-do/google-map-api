package com.example.do_f.taxiaz.api.post;

/**
 * Created by do_f on 28/01/16.
 */
public class    User
{
    private String  mail;
    private String  phone;
    private String  address;
    private Double  longitude;
    private Double  latitude;

    public User(String mail, String phone, String address, Double longitude, Double latitude) {
        this.mail = mail;
        this.phone = phone;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLng() {
        return longitude;
    }

    public void setLng(Double lng) {
        this.longitude = lng;
    }

    public Double getLat() {
        return latitude;
    }

    public void setLat(Double lat) {
        this.latitude = lat;
    }
}
