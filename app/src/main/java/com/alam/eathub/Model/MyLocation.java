package com.alam.eathub.Model;

import com.alam.eathub.Model.Cordinates.Cords;

import java.util.List;

public class MyLocation {
    private String address , locality , city , zipcode , locality_verbose;
    private  int country_id , city_id;
    private List<Cords> cords;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getLocality_verbose() {
        return locality_verbose;
    }

    public void setLocality_verbose(String locality_verbose) {
        this.locality_verbose = locality_verbose;
    }

    public int getCountry_id() {
        return country_id;
    }

    public void setCountry_id(int country_id) {
        this.country_id = country_id;
    }

    public int getCity_id() {
        return city_id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }

    public List<Cords> getCords() {
        return cords;
    }

    public void setCords(List<Cords> cords) {
        this.cords = cords;
    }
}
