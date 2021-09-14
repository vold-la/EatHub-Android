package com.alam.eathub.Model.My;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchedRestaurant {
    @SerializedName("err")
    @Expose
    private Boolean err;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("result")
    @Expose
    private List<Rrestaurant> restaurant = null;

    public Boolean getErr() {
        return err;
    }

    public void setErr(Boolean err) {
        this.err = err;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Rrestaurant> getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(List<Rrestaurant> restaurant) {
        this.restaurant = restaurant;
    }
}